package com.anantwave;

import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class DataEncryptor {

    static final String DB_URL = "jdbc:mysql://localhost:3306/testdb"; // MySQL DB connection
    static final String USER = "root"; // DB username
    static final String PASS = "Vishnu@289"; // DB password

    public static void main(String[] args) throws Exception {
        String inputCsv = "input.csv"; // Input CSV file
        String outputCsv = "output.csv"; // Output CSV file

        // Step 1: Generate and Load AES Key
        // Instead of generating key locally, we load or generate it via Cloud KMS
        byte[] keyBytes = EncryptionUtils.generateAESKey(); // This is still local generation of AES key
        String encryptedKey = EncryptionUtils.encryptAESKeyWithKMS(keyBytes); // Encrypt the AES key with KMS

        // Step 2: Read CSV and Encrypt Data
        List<String[]> encryptedRows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(inputCsv))) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                String[] encryptedRow = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    // Encrypt the data using AES (but using the AES key)
                    encryptedRow[i] = EncryptionUtils.encrypt(row[i], new SecretKeySpec(keyBytes, "AES"));
                }
                encryptedRows.add(encryptedRow);
            }
        }

        // Step 3: Store Encrypted Data in MySQL
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS encrypted_data (col1 TEXT, col2 TEXT)");

            PreparedStatement ps = conn.prepareStatement("INSERT INTO encrypted_data (col1, col2) VALUES (?, ?)");
            for (String[] row : encryptedRows) {
                ps.setString(1, row[0]);
                ps.setString(2, row[1]);
                ps.executeUpdate();
            }
        }

        // Step 4: Read Encrypted Data from MySQL and Decrypt
        List<String[]> decryptedRows = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT col1, col2 FROM encrypted_data");
            while (rs.next()) {
                // Decrypt the data using AES key from KMS
                String col1 = EncryptionUtils.decrypt(rs.getString("col1"), new SecretKeySpec(keyBytes, "AES"));
                String col2 = EncryptionUtils.decrypt(rs.getString("col2"), new SecretKeySpec(keyBytes, "AES"));
                decryptedRows.add(new String[]{col1, col2});
            }
        }

        // Step 5: Write Decrypted Data to CSV
        try (CSVWriter writer = new CSVWriter(new FileWriter(outputCsv))) {
            for (String[] row : decryptedRows) {
                writer.writeNext(row);
            }
        }

        System.out.println("✅ Process complete. Decrypted output written to output.csv");
    }
}

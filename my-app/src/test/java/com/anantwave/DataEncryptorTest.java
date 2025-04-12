// package com.anantwave;

// import static org.junit.Assert.*;
// import static org.mockito.Mockito.*;

// import org.junit.Before;
// import org.junit.Test;

// import java.util.List;
// import java.util.ArrayList;

// import com.opencsv.CSVWriter;

// public class DataEncryptorTest {

//     private DataEncryptor dataEncryptor;

//     @Before
//     public void setUp() {
//         // Mocking DataEncryptor class
//         dataEncryptor = mock(DataEncryptor.class);  
//     }

//     @Test
//     public void testFullProcess() throws Exception {
//         // Mock the behavior of the methods
//         List<String[]> mockedEncryptedRows = new ArrayList<>();
//         mockedEncryptedRows.add(new String[]{"Encrypted1", "Encrypted2"});
//         when(dataEncryptor.readCsvAndEncrypt(anyString(), any(byte[].class))).thenReturn(mockedEncryptedRows);

//         doNothing().when(dataEncryptor).storeEncryptedDataInDatabase(anyList());

//         List<String[]> mockedDecryptedRows = new ArrayList<>();
//         mockedDecryptedRows.add(new String[]{"Decrypted1", "Decrypted2"});
//         when(dataEncryptor.readDecryptedDataFromDatabase(any(byte[].class))).thenReturn(mockedDecryptedRows);

//         // Mock the CSVWriter behavior
//         CSVWriter writer = mock(CSVWriter.class);
//         doNothing().when(writer).writeNext(any(String[].class));

//         // Call the method and verify behavior
//         dataEncryptor.readCsvAndEncrypt("input.csv", new byte[32]);
//         dataEncryptor.storeEncryptedDataInDatabase(mockedEncryptedRows);
//         dataEncryptor.readDecryptedDataFromDatabase(new byte[32]);
//         dataEncryptor.writeCsv("output.csv", mockedDecryptedRows);

//         // Verify all the methods were called correctly
//         verify(dataEncryptor, times(1)).readCsvAndEncrypt("input.csv", new byte[32]);
//         verify(dataEncryptor, times(1)).storeEncryptedDataInDatabase(mockedEncryptedRows);
//         verify(dataEncryptor, times(1)).readDecryptedDataFromDatabase(new byte[32]);
//         verify(writer, times(1)).writeNext(new String[]{"Decrypted1", "Decrypted2"});
//     }
// }

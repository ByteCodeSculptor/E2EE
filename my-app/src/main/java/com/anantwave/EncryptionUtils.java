package com.anantwave;


import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import com.google.cloud.kms.v1.DecryptRequest;
import com.google.cloud.kms.v1.DecryptResponse;
import com.google.cloud.kms.v1.EncryptRequest;
import com.google.cloud.kms.v1.EncryptResponse;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.protobuf.ByteString;

public class EncryptionUtils {

    private static final String KEY_RING = "projects/carbide-atlas-456623-f5/locations/global/keyRings/my-keyring/cryptoKeys/my-key/cryptoKeyVersions/1"; // Replacing with your KMS key path
    private static final String CLOUD_KMS_ENDPOINT = "cloudkms.googleapis.com";

    // Use Google Cloud KMS to encrypt the AES key
    public static String encryptAESKeyWithKMS(byte[] aesKey) throws Exception {
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            EncryptRequest request = EncryptRequest.newBuilder()
                    .setName(KEY_RING)
                    .setPlaintext(ByteString.copyFrom(aesKey))
                    .build();

            // Encrypt the AES key using Google Cloud KMS
            EncryptResponse response = client.encrypt(request);
            return response.getCiphertext().toStringUtf8();
        }
    }

    // Use Google Cloud KMS to decrypt the AES key
    public static byte[] decryptAESKeyWithKMS(String encryptedKey) throws Exception {
        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            DecryptRequest request = DecryptRequest.newBuilder()
                    .setName(KEY_RING)
                    .setCiphertext(ByteString.copyFromUtf8(encryptedKey))
                    .build();

            // Decrypt the AES key using Google Cloud KMS
            DecryptResponse response = client.decrypt(request);
            return response.getPlaintext().toByteArray();
        }
    }

    // Generate AES key (local key generation)
    public static byte[] generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // AES-256
        return keyGen.generateKey().getEncoded();
    }

    // Encrypt data using AES
    public static String encrypt(String plainText, SecretKey key) throws Exception {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // AES encryption with CBC mode
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(plainText.getBytes());

        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    // Decrypt data using AES
    public static String decrypt(String encryptedText, SecretKey key) throws Exception {
        byte[] combined = Base64.getDecoder().decode(encryptedText);
        byte[] iv = new byte[16];
        byte[] encrypted = new byte[combined.length - 16];

        System.arraycopy(combined, 0, iv, 0, 16);
        System.arraycopy(combined, 16, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] original = cipher.doFinal(encrypted);

        return new String(original); // Decrypt and return original data
    }
}

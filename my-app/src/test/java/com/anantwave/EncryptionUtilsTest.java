package com.anantwave;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.cloud.kms.v1.*;
import com.google.protobuf.ByteString;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtilsTest {

    private EncryptionUtils encryptionUtils;

    @Before
    public void setUp() {
        encryptionUtils = new EncryptionUtils();
    }

    @Test
    public void testGenerateAESKey() throws Exception {
        // Test key generation method
        byte[] key = EncryptionUtils.generateAESKey();
        assertNotNull(key);
        assertEquals(32, key.length);  // AES-256 should generate 32-byte keys
    }

    @Test
    public void testEncryptAndDecrypt() throws Exception {
        // Test the encryption and decryption of a string
        String originalText = "Sensitive Data";
        byte[] keyBytes = EncryptionUtils.generateAESKey();
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        String encryptedText = EncryptionUtils.encrypt(originalText, keySpec);
        assertNotNull(encryptedText);
        
        String decryptedText = EncryptionUtils.decrypt(encryptedText, keySpec);
        assertNotNull(decryptedText);
        
        assertEquals(originalText, decryptedText);
    }

    @Test
    public void testEncryptAESKeyWithKMS() throws Exception {
        // Mock Google Cloud KMS client
        KeyManagementServiceClient client = mock(KeyManagementServiceClient.class);
        EncryptResponse encryptResponse = mock(EncryptResponse.class);
        
        // Simulate Google Cloud KMS encryption
        String encryptedKey = "mockEncryptedKey";
        when(client.encrypt(any(EncryptRequest.class))).thenReturn(encryptResponse);
        when(encryptResponse.getCiphertext()).thenReturn(ByteString.copyFromUtf8(encryptedKey));

        byte[] aesKey = new byte[32]; // Sample AES key
        String result = EncryptionUtils.encryptAESKeyWithKMS(aesKey);

        assertNotNull(result);
        assertEquals(encryptedKey, result);  // Should match the mock encrypted key
    }

    @Test
    public void testDecryptAESKeyWithKMS() throws Exception {
        // Mock Google Cloud KMS client
        KeyManagementServiceClient client = mock(KeyManagementServiceClient.class);
        DecryptResponse decryptResponse = mock(DecryptResponse.class);
        
        // Simulate Google Cloud KMS decryption
        byte[] decryptedKey = new byte[32]; // Mock decrypted key
        when(client.decrypt(any(DecryptRequest.class))).thenReturn(decryptResponse);
        when(decryptResponse.getPlaintext()).thenReturn(ByteString.copyFrom(decryptedKey));

        String encryptedKey = "mockEncryptedKey";
        byte[] result = EncryptionUtils.decryptAESKeyWithKMS(encryptedKey);

        assertNotNull(result);
        assertArrayEquals(decryptedKey, result);  // Should match the mock decrypted key
    }

    @Test
    public void testEncryptionWithValidKey() throws Exception {
        String plainText = "Hello, World!";
        byte[] keyBytes = EncryptionUtils.generateAESKey();
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        // Encrypt the text
        String encryptedText = EncryptionUtils.encrypt(plainText, keySpec);
        assertNotNull(encryptedText);

        // Decrypt the text
        String decryptedText = EncryptionUtils.decrypt(encryptedText, keySpec);
        assertNotNull(decryptedText);
        assertEquals(plainText, decryptedText); // Should match the original text
    }
}

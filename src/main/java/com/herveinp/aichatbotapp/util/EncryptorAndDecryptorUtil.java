package com.herveinp.aichatbotapp.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;

public class EncryptorAndDecryptorUtil {
	private static SecretKey secretKey;
	private static final String ALGORITHM = "AES";

	// Generate AES key (128-bit)
	public static SecretKey generateKey() throws Exception {
		KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
		keyGen.init(128);
		return keyGen.generateKey();
	}
	
	// getSecretKey()
	public static SecretKey getSecretKey() throws Exception {
		if (secretKey == null) {
			secretKey = generateKey();
		}
		
		return secretKey;
	}
	
	// Encrypt plain text and encode with Apache Commons Codec Base64
    public static String encrypt(String plainText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.encodeBase64String(encryptedBytes);
    }
    
    // Decode Base64 and decrypt cipher text
    public static String decrypt(String cipherText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.decodeBase64(cipherText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, "UTF-8");
    }
}

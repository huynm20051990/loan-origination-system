package com.loan.origination.system.util.encryption;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {
  private static final String ALGORITHM = "AES";
  // In production, move this to an environment variable or Secret Manager!
  private static final String KEY = "MySuperSecretKey";

  public static String encrypt(String data) throws Exception {
    SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
  }

  public static String encryptQuietly(String data) {
    try {
      return encrypt(data);
    } catch (Exception e) {
      throw new RuntimeException("Encryption failed", e);
    }
  }

  public static String decrypt(String encryptedData) throws Exception {
    SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
  }

  /**
   * Decrypts the data and wraps any checked exceptions into a RuntimeException. Useful for cleaner
   * code in Tool methods.
   */
  public static String decryptQuietly(String encryptedData) {
    try {
      return decrypt(encryptedData);
    } catch (Exception e) {
      throw new RuntimeException(
          "Decryption failed! The provided data was either malformed or tampered with.", e);
    }
  }
}

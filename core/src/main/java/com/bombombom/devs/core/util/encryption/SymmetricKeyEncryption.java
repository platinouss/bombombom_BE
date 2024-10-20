package com.bombombom.devs.core.util.encryption;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public interface SymmetricKeyEncryption {

    SecretKey generateSecretKey() throws NoSuchAlgorithmException;

    byte[] encrypt(byte[] data, byte[] iv, SecretKey secretKey)
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException;

    byte[] decrypt(SecretKey secretKey, byte[] iv, String encryptedData)
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException;

    String serializeSymmetricKey(SecretKey symmetricKey);

    SecretKey deserializeSymmetricKey(String symmetricKey);
}

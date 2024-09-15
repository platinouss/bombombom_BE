package com.bombombom.devs.core.util.encryption;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public interface AsymmetricKeyEncryption {

    KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;

    byte[] encrypt(byte[] data, PublicKey publicKey)
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException;

    byte[] decrypt(byte[] encryptedData, PrivateKey privateKey)
        throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException;

    PublicKey deserializePublicKey(String publicKey) throws InvalidKeySpecException;

    PrivateKey deserializePrivateKey(String privateKey) throws InvalidKeySpecException;
}

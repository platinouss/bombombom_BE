package com.bombombom.devs.core.util.encryption;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAEncryption implements AsymmetricKeyEncryption {

    private static final KeyFactory keyFactory;

    static {
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    @Override
    public byte[] encrypt(byte[] data, PublicKey publicKey)
        throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    @Override
    public byte[] decrypt(byte[] encryptedData, PrivateKey privateKey)
        throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }

    @Override
    public String serializePublicKey(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    @Override
    public String serializePrivateKey(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    @Override
    public PublicKey deserializePublicKey(String publicKey) throws InvalidKeySpecException {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
        return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
    }

    @Override
    public PrivateKey deserializePrivateKey(String privateKey) throws InvalidKeySpecException {
        byte[] privateBytes = Base64.getDecoder().decode(privateKey);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
    }
}

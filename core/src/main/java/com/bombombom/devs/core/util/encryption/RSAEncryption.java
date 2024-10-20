package com.bombombom.devs.core.util.encryption;

import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.ServerInternalException;
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
import javax.crypto.Cipher;

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
    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new ServerInternalException(ErrorCode.UNEXPECTED_EXCEPTION);
        }
    }

    @Override
    public byte[] encrypt(byte[] data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new ServerInternalException(ErrorCode.UNEXPECTED_EXCEPTION);
        }
    }

    @Override
    public byte[] decrypt(byte[] encryptedData, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            throw new ServerInternalException(ErrorCode.UNEXPECTED_EXCEPTION);
        }
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
    public PublicKey deserializePublicKey(String publicKey) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
            return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        } catch (InvalidKeySpecException e) {
            throw new ServerInternalException(ErrorCode.UNEXPECTED_EXCEPTION);
        }
    }

    @Override
    public PrivateKey deserializePrivateKey(String privateKey) {
        try {
            byte[] privateBytes = Base64.getDecoder().decode(privateKey);
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
        } catch (InvalidKeySpecException e) {
            throw new ServerInternalException(ErrorCode.UNEXPECTED_EXCEPTION);
        }
    }

    @Override
    public KeyPair toKeyPair(String serializedPublicKey, String serializedPrivateKey) {
        PublicKey publicKey = deserializePublicKey(serializedPublicKey);
        PrivateKey privateKey = deserializePrivateKey(serializedPrivateKey);
        return new KeyPair(publicKey, privateKey);
    }
}

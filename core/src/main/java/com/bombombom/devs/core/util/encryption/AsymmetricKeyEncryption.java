package com.bombombom.devs.core.util.encryption;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface AsymmetricKeyEncryption {

    KeyPair generateKeyPair();

    byte[] encrypt(byte[] data, PublicKey publicKey);

    byte[] decrypt(byte[] encryptedData, PrivateKey privateKey);

    String serializePublicKey(PublicKey publicKey);

    String serializePrivateKey(PrivateKey privateKey);

    PublicKey deserializePublicKey(String publicKey);

    PrivateKey deserializePrivateKey(String privateKey);

    KeyPair toKeyPair(String serializedPublicKey, String serializedPrivateKey);
}

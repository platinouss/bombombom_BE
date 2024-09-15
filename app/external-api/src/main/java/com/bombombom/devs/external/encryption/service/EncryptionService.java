package com.bombombom.devs.external.encryption.service;

import com.bombombom.devs.core.util.encryption.AsymmetricKeyEncryption;
import com.bombombom.devs.core.util.encryption.RSAEncryption;
import com.bombombom.devs.encryption.model.vo.PrivateKeyInfo;
import com.bombombom.devs.encryption.model.vo.PublicKeyInfo;
import com.bombombom.devs.encryption.repository.EncryptionRedisRepository;
import com.bombombom.devs.external.encryption.service.dto.PublicKeyResult;
import jakarta.annotation.PostConstruct;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EncryptionService {

    static final int PUBLIC_KEY_MAX_COUNT = 10;
    static final int ASYMMETRIC_KEY_EXPIRY_DURATION_MINUTES = 5;

    private final EncryptionRedisRepository encryptionRedisRepository;
    private final AsymmetricKeyEncryption asymmetricKeyEncryption = new RSAEncryption();

    @PostConstruct
    private void init() {
        for (int id = 1; id <= PUBLIC_KEY_MAX_COUNT; id++) {
            if (!encryptionRedisRepository.hasPublicKeyById(id)) {
                addNewAsymmetricKeyPair(id, 1.0, generateKeyPair());
            }
        }
    }

    public void addNewAsymmetricKeyPair(int id, double version, KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        encryptionRedisRepository.addAsymmetricKeyPair(id, version, publicKeyStr, privateKeyStr);
        encryptionRedisRepository.updateAsymmetricKeyPairVersion(id, version);
        setExpirationForPreviousAsymmetricKeyPair(id, version);
    }

    public PublicKeyResult getRandomPublicKeyInfo() {
        int id = new Random().nextInt(PUBLIC_KEY_MAX_COUNT) + 1;
        double latestVersion = encryptionRedisRepository.getAsymmetricKeyPairLatestVersion(id);
        PublicKeyInfo publicKeyInfo = encryptionRedisRepository.getPublicKeyAndVersionById(id,
            latestVersion);
        return PublicKeyResult.fromEntry(id, latestVersion, publicKeyInfo);
    }

    public String decryptData(int id, double version, byte[] encryptedData)
        throws InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        PrivateKeyInfo privateKeyInfo = encryptionRedisRepository.getPrivateKeyAndVersionById(id,
            version);
        PrivateKey privateKey = asymmetricKeyEncryption.deserializePrivateKey(
            privateKeyInfo.privateKey());
        return new String(asymmetricKeyEncryption.decrypt(encryptedData, privateKey));
    }

    private KeyPair generateKeyPair() {
        KeyPair keyPair;
        try {
            keyPair = asymmetricKeyEncryption.generateKeyPair();
        } catch (Exception e) {
            log.error("Failed to generate asymmetric key pair. Error details: ", e);
            throw new RuntimeException();
        }
        return keyPair;
    }

    private void setExpirationForPreviousAsymmetricKeyPair(int id, double version) {
        if (version == 1.0) {
            return;
        }
        encryptionRedisRepository.setAsymmetricKeyPairExpirationInMinutes(id, version - 0.1,
            ASYMMETRIC_KEY_EXPIRY_DURATION_MINUTES);
    }
}

package com.bombombom.devs.external.encryption.service;

import com.bombombom.devs.core.util.encryption.AsymmetricKeyEncryption;
import com.bombombom.devs.encryption.model.vo.PrivateKeyInfo;
import com.bombombom.devs.encryption.model.vo.PublicKeyInfo;
import com.bombombom.devs.encryption.repository.AsymmetricEncryptionRedisRepository;
import com.bombombom.devs.external.encryption.service.dto.PublicKeyResult;
import com.bombombom.devs.external.global.circuitbreaker.CircuitBreakerProvider;
import com.bombombom.devs.external.global.circuitbreaker.RetryProvider;
import com.bombombom.devs.external.global.decryption.InMemoryAsymmetricKeyManager;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PostConstruct;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
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
public class AsymmetricEncryptionService {

    static final int PUBLIC_KEY_MAX_COUNT = 10;
    static final int ASYMMETRIC_KEY_EXPIRY_DURATION_MINUTES = 5;

    private final AsymmetricKeyEncryption asymmetricKeyEncryption;
    private final InMemoryAsymmetricKeyManager inMemoryAsymmetricKeyManager;
    private final AsymmetricEncryptionRedisRepository asymmetricEncryptionRedisRepository;

    @PostConstruct
    private void init() {
        for (int id = 1; id <= PUBLIC_KEY_MAX_COUNT; id++) {
            if (!asymmetricEncryptionRedisRepository.hasPublicKeyById(id)) {
                addNewAsymmetricKey(id, 1);
            }
        }
    }

    public String decryptData(int id, long version, byte[] encryptedData)
        throws InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (id == 0) {
            PrivateKey privateKey = inMemoryAsymmetricKeyManager.getAsymmetricKeyByVersion(version)
                .getPrivate();
            return new String(asymmetricKeyEncryption.decrypt(encryptedData, privateKey));
        }
        PrivateKeyInfo privateKeyInfo = asymmetricEncryptionRedisRepository.getPrivateKeyAndVersionById(
            id, version);
        PrivateKey privateKey = asymmetricKeyEncryption.deserializePrivateKey(
            privateKeyInfo.privateKey());
        return new String(asymmetricKeyEncryption.decrypt(encryptedData, privateKey));
    }

    @Retry(name = RetryProvider.RETRY_REDIS)
    @CircuitBreaker(name = CircuitBreakerProvider.CIRCUIT_REDIS, fallbackMethod = "getFallbackPublicKeyInfo")
    public PublicKeyResult getRandomPublicKeyInfo() {
        int id = new Random().nextInt(PUBLIC_KEY_MAX_COUNT) + 1;
        long latestVersion = asymmetricEncryptionRedisRepository.getAsymmetricKeyPairLatestVersion(
            id);
        PublicKeyInfo publicKeyInfo = asymmetricEncryptionRedisRepository.getPublicKeyAndVersionById(
            id, latestVersion);
        return PublicKeyResult.fromEntry(id, latestVersion, publicKeyInfo.publicKey());
    }

    public PublicKeyResult getFallbackPublicKeyInfo(Throwable e) {
        long currentSymmetricKeyVersion = inMemoryAsymmetricKeyManager.getLatestSymmetricKeyVersion();
        PublicKey publicKey = inMemoryAsymmetricKeyManager.getAsymmetricKeyByVersion(
            currentSymmetricKeyVersion).getPublic();
        String serializedPublicKey = asymmetricKeyEncryption.serializePublicKey(publicKey);
        return PublicKeyResult.fromEntry(0, currentSymmetricKeyVersion, serializedPublicKey);
    }

    private KeyPair generateAsymmetricKeyPair() {
        KeyPair keyPair;
        try {
            keyPair = asymmetricKeyEncryption.generateKeyPair();
        } catch (Exception e) {
            log.error("Failed to generate asymmetric key pair. Error details: ", e);
            throw new RuntimeException();
        }
        return keyPair;
    }

    private void addNewAsymmetricKey(int id, long version) {
        KeyPair keyPair = generateAsymmetricKeyPair();
        String serializedPublicKey = asymmetricKeyEncryption.serializePublicKey(
            keyPair.getPublic());
        String serializedPrivateKey = asymmetricKeyEncryption.serializePrivateKey(
            keyPair.getPrivate());
        asymmetricEncryptionRedisRepository.addAsymmetricKeyPair(id, version, serializedPublicKey,
            serializedPrivateKey);
        asymmetricEncryptionRedisRepository.updateAsymmetricKeyPairVersion(id, version);
        setExpirationForPreviousAsymmetricKeyPair(id, version);
    }

    private void setExpirationForPreviousAsymmetricKeyPair(int id, long version) {
        if (version == 1) {
            return;
        }
        asymmetricEncryptionRedisRepository.setAsymmetricKeyPairExpirationInMinutes(id,
            version - 1, ASYMMETRIC_KEY_EXPIRY_DURATION_MINUTES);
    }
}

package com.bombombom.devs.external.global.decryption;

import com.bombombom.devs.core.util.encryption.AsymmetricKeyEncryption;
import com.bombombom.devs.encryption.model.AsymmetricKey;
import com.bombombom.devs.encryption.repository.AsymmetricKeyRepository;
import com.bombombom.devs.external.encryption.service.dto.AsymmetricKeyResult;
import jakarta.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryAsymmetricKeyManager {

    private final AtomicLong currentAsymmetricVersion = new AtomicLong();
    private final ConcurrentHashMap<Long, KeyPair> asymmetricKeys = new ConcurrentHashMap<>();
    private final AsymmetricKeyEncryption asymmetricKeyEncryption;
    private final AsymmetricKeyRepository asymmetricKeyRepository;

    @PostConstruct
    public void init() {
        if (asymmetricKeyRepository.count() == 0) {
            addNewAsymmetricKey();
        }
        saveAsymmetricKeys();
    }

    public Long getLatestSymmetricKeyVersion() {
        long currentVersion = currentAsymmetricVersion.get();
        if (currentVersion == 0) {
            throw new RuntimeException("비대칭키가 존재하지 않습니다.");
        }
        return currentAsymmetricVersion.get();
    }

    public KeyPair getAsymmetricKeyByVersion(long version) {
        return asymmetricKeys.get(version);
    }

    public void saveAsymmetricKeys() {
        List<AsymmetricKeyResult> asymmetricKeyResults = getFallbackAsymmetricKeys();
        asymmetricKeyResults.forEach(
            result -> asymmetricKeys.put(result.version(), result.keyPair()));
        long currentVersion = asymmetricKeyResults.stream().map(AsymmetricKeyResult::version)
            .max(Long::compareTo).orElse(0L);
        currentAsymmetricVersion.set(currentVersion);
    }
    
    private void addNewAsymmetricKey() {
        KeyPair keyPair = generateAsymmetricKeyPair();
        String serializedPublicKey = asymmetricKeyEncryption.serializePublicKey(
            keyPair.getPublic());
        String serializedPrivateKey = asymmetricKeyEncryption.serializePrivateKey(
            keyPair.getPrivate());
        asymmetricKeyRepository.save(
            AsymmetricKey.generate(serializedPublicKey, serializedPrivateKey));
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

    private List<AsymmetricKeyResult> getFallbackAsymmetricKeys() {
        List<AsymmetricKey> asymmetricKeys = asymmetricKeyRepository.findAll();
        return asymmetricKeys.stream().map(
            asymmetricKey -> {
                try {
                    PublicKey publicKey = asymmetricKeyEncryption.deserializePublicKey(
                        asymmetricKey.getPublicKey());
                    PrivateKey privateKey = asymmetricKeyEncryption.deserializePrivateKey(
                        asymmetricKey.getPrivateKey());
                    return AsymmetricKeyResult.fromEntity(asymmetricKey.getId(), publicKey,
                        privateKey);
                } catch (InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                }
            }
        ).toList();
    }
}

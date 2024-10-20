package com.bombombom.devs.external.global.decryption;

import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.core.util.encryption.AsymmetricKeyEncryption;
import com.bombombom.devs.encryption.model.AsymmetricKey;
import com.bombombom.devs.encryption.repository.AsymmetricKeyRepository;
import com.bombombom.devs.external.encryption.service.dto.AsymmetricKeyResult;
import jakarta.annotation.PostConstruct;
import java.security.KeyPair;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 인메모리에서 비대칭 키를 관리하기 위한 매니저이다.
 * <p>
 * DB에서 인메모리에 추가 할 비대칭 키 정보를 가져온 후, {@code asymmetricKeys}에 비대칭 키를 version 값에 매핑되도록 저장하고,
 * {@code currentAsymmetricVersion}에는 최신 version 값을 저장한다. </p>
 *
 * @see <a href="https://github.com/Team-BomBomBom/Server/pull/57">Feat: #BBB-136 로그인 및 회원가입 시
 * 클라이언트와 서버 간 종단간 암호화 적용</a>
 */

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
            initAsymmetricKey();
        }
        addAsymmetricKeysInMemory();
    }

    public Long getLatestAsymmetricKeyVersion() {
        return currentAsymmetricVersion.get();
    }

    public KeyPair getAsymmetricKeyByVersion(long version) {
        return asymmetricKeys.get(version);
    }

    public void addAsymmetricKeysInMemory() {
        List<AsymmetricKeyResult> asymmetricKeyResults = getFallbackAsymmetricKeys();
        asymmetricKeyResults.forEach(
            result -> asymmetricKeys.put(result.version(), result.keyPair()));
        long currentVersion = asymmetricKeyResults.stream().map(AsymmetricKeyResult::version)
            .max(Long::compareTo).orElse(0L);
        currentAsymmetricVersion.set(currentVersion);
    }

    public void addAsymmetricKeyInMemory(Long version) {
        if (asymmetricKeys.containsKey(version)) {
            return;
        }
        AsymmetricKey asymmetricKey = asymmetricKeyRepository.findById(version)
            .orElseThrow(() -> new NotFoundException(ErrorCode.ASYMMETRIC_KEY_NOT_FOUND));
        KeyPair keyPair = asymmetricKeyEncryption.toKeyPair(asymmetricKey.getPublicKey(),
            asymmetricKey.getPrivateKey());
        asymmetricKeys.put(version, keyPair);
    }

    private void initAsymmetricKey() {
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
                KeyPair keyPair = asymmetricKeyEncryption.toKeyPair(
                    asymmetricKey.getPublicKey(), asymmetricKey.getPrivateKey());
                return AsymmetricKeyResult.fromEntity(asymmetricKey.getId(), keyPair);
            }
        ).toList();
    }
}

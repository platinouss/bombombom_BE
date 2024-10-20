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
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * HTTPS 환경에서도 중간자 공격 등의 위협에 대비하여 민감한 데이터를 안전하게 전송하기 위해 클라이언트와 서버 간 종단간 암호화를 적용한다.
 * <p>
 * {@link AsymmetricEncryptionService#getRandomPublicKeyInfo()}메서드가 호출되면, Redis에 저장된
 * {@code PUBLIC_KEY_MAX_COUNT} 개의 비대칭 키 중 무작위로 하나를 선택하여, 해당 키의 정보(version, public Key)를 가져와
 * 응답한다.</p>
 * <p>
 * 만약 Redis에 장애가 발생한 경우(Circuit Breaker가 OPEN된 경우), Fallback Method로 설정된
 * {@link AsymmetricEncryptionService#getFallbackPublicKeyInfo(Throwable)}가 호출되고,
 * {@link InMemoryAsymmetricKeyManager}를 통해 인메모리에 저장된 비대칭 키 정보(version, public key)를 응답하여 Redis에 장애가
 * 발생한 경우에도 종단간 암호화가 적용되도록 보장한다. </p>
 *
 * @see <a href="https://github.com/Team-BomBomBom/Server/pull/57">Feat: #BBB-136 로그인 및 회원가입 시
 * 클라이언트와 서버 간 종단간 암호화 적용</a>
 */

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

    /**
     * id와 version 정보에 매핑되는 private key를 Redis에서(id가 0인 경우는 인메모리에서)찾고, 해당 private key로
     * {@code encryptedData}를 복호화한 후 응답한다.
     *
     * @param id            비대칭 키를 식별하기 위한 값 (0인 경우는 인메모리에 저장된 비대칭 키를 의미)
     * @param version       특정 id를 가진 비대칭 키의 버전 정보
     * @param encryptedData 특정 id와 version에 매핑되는 public key로 암호화된 데이터
     * @return encryptedData를 복호화 한 데이터
     */
    public String decryptData(int id, long version, byte[] encryptedData) {
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

    /**
     * Redis에서 특정 id의 비대칭 키에 대한 최신 version 값을 가져온 후, 해당 id와 version에 매핑되는 public key를 가져온다. 이후 id,
     * version, public key를 응답한다.
     *
     * @return PublicKeyResult
     */
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

    /**
     * Redis에 장애가 발생한 경우(Circuit Breaker가 OPEN된 경우) 호출되는 메서드로, 인메모리에 저장된 비대칭 키 정보에서 현재 최신 version 값과
     * public key를 가져온다. 이때 id는 0으로 고정되고, version, public key를 응답한다.
     *
     * @return PublicKeyResult
     */
    public PublicKeyResult getFallbackPublicKeyInfo(Throwable e) {
        long currentSymmetricKeyVersion = inMemoryAsymmetricKeyManager.getLatestAsymmetricKeyVersion();
        PublicKey publicKey = inMemoryAsymmetricKeyManager.getAsymmetricKeyByVersion(
            currentSymmetricKeyVersion).getPublic();
        String serializedPublicKey = asymmetricKeyEncryption.serializePublicKey(publicKey);
        return PublicKeyResult.fromEntry(0, currentSymmetricKeyVersion, serializedPublicKey);
    }

    private KeyPair generateAsymmetricKeyPair() {
        return asymmetricKeyEncryption.generateKeyPair();
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

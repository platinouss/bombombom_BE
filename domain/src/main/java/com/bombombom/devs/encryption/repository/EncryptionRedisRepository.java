package com.bombombom.devs.encryption.repository;

import static com.bombombom.devs.common.constant.EncryptionRedisConstant.getRedisKeyForAsymmetricKeyPair;
import static com.bombombom.devs.common.constant.EncryptionRedisConstant.getRedisKeyForAsymmetricKeyPairVersion;

import com.bombombom.devs.encryption.model.vo.PrivateKeyInfo;
import com.bombombom.devs.encryption.model.vo.PublicKeyInfo;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EncryptionRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, String, String> hashOperations;

    public boolean hasPublicKeyById(int id) {
        String redisKey = getRedisKeyForAsymmetricKeyPairVersion(id);
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }

    public void addAsymmetricKeyPair(int id, double version, String publicKey, String privateKey) {
        String redisKey = getRedisKeyForAsymmetricKeyPair(id, version);
        hashOperations.putAll(redisKey, Map.of(
            "publicKey", publicKey,
            "privateKey", privateKey
        ));
    }

    public void setAsymmetricKeyPairExpirationInMinutes(int id, double version, long timeout) {
        String redisKey = getRedisKeyForAsymmetricKeyPair(id, version);
        redisTemplate.expire(redisKey, timeout, TimeUnit.MINUTES);
    }

    public void updateAsymmetricKeyPairVersion(int id, double version) {
        String redisKey = getRedisKeyForAsymmetricKeyPairVersion(id);
        redisTemplate.opsForValue().set(redisKey, String.valueOf(version));
    }

    public double getAsymmetricKeyPairLatestVersion(int id) {
        String redisKey = getRedisKeyForAsymmetricKeyPairVersion(id);
        return Double.parseDouble(
            Objects.requireNonNull(redisTemplate.opsForValue().get(redisKey)));
    }

    public PublicKeyInfo getPublicKeyAndVersionById(int id, double version) {
        String redisKey = getRedisKeyForAsymmetricKeyPair(id, version);
        return PublicKeyInfo.fromResult(hashOperations.entries(redisKey));
    }

    public PrivateKeyInfo getPrivateKeyAndVersionById(int id, double version) {
        String redisKey = getRedisKeyForAsymmetricKeyPair(id, version);
        return PrivateKeyInfo.fromResult(hashOperations.entries(redisKey));
    }
}

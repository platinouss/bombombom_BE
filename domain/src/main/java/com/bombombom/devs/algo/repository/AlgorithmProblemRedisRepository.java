package com.bombombom.devs.algo.repository;

import com.bombombom.devs.core.util.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AlgorithmProblemRedisRepository {

    static final String SOLVEDAC_BUCKET_CREATION_KEY = "bucket:solvedac:created_at";

    private final Clock clock;
    private final RedisTemplate<String, String> redisTemplate;
    private final ValueOperations<String, Object> valueOperations;

    public void setBucketCreationTimeIfAbsent() {
        Boolean existKey = redisTemplate.hasKey(SOLVEDAC_BUCKET_CREATION_KEY);
        if (Optional.ofNullable(existKey).orElse(false)) {
            return;
        }
        Long currentTime = clock.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        valueOperations.append(SOLVEDAC_BUCKET_CREATION_KEY, String.valueOf(currentTime));
    }

    public LocalDateTime getBucketCreationTime() {
        String bucketCreationTime = (String) Optional.ofNullable(
                valueOperations.get(SOLVEDAC_BUCKET_CREATION_KEY))
            .orElseThrow(() -> new NoSuchElementException(
                "No value found for bucket key: " + SOLVEDAC_BUCKET_CREATION_KEY));
        Instant instant = Instant.ofEpochMilli(Long.parseLong(bucketCreationTime));
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}

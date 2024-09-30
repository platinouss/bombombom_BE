package com.bombombom.devs.ratelimit;

import com.bombombom.devs.algo.repository.AlgorithmProblemRedisRepository;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiRateLimiter {

    private final ProxyManager<String> proxyManager;
    private final AlgorithmProblemRedisRepository algorithmProblemRedisRepository;
    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public boolean tryConsume(String apiKey, BucketConfiguration configuration) {
        Bucket bucket = createOrGetBucket(apiKey, configuration);
        return bucket.tryConsume(1);
    }

    public boolean tryConsume(String apiKey, BucketConfiguration configuration, int count) {
        Bucket bucket = createOrGetBucket(apiKey, configuration);
        return bucket.tryConsume(count);
    }

    private Bucket createOrGetBucket(String apiKey, BucketConfiguration configuration) {
        return buckets.computeIfAbsent(apiKey,
            key -> {
                BucketProxy bucketProxy = proxyManager.getProxy(key, () -> configuration);
                algorithmProblemRedisRepository.setBucketCreationTimeIfAbsent();
                return bucketProxy;
            });
    }
}

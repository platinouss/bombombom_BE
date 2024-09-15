package com.bombombom.devs.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

    private final RedisConnectionFactory redisConnectionFactory;

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private String port;

    @Value("${spring.data.redis.ssl.enabled}")
    private boolean isSslEnabled;

    @Bean
    RedisClient redisClient() {
        RedisURI redisUri = RedisURI.Builder.redis(host, Integer.parseInt(port))
            .withSsl(isSslEnabled)
            .build();
        return RedisClient.create(redisUri);
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    ValueOperations<String, Object> valueOperations() {
        return redisTemplate().opsForValue();
    }

    @Bean
    HashOperations<String, String, String> hashOperations() {
        return redisTemplate().opsForHash();
    }

    @Bean
    StreamOperations<String, String, String> streamOperations() {
        return redisTemplate().opsForStream();
    }
}
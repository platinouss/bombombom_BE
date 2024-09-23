package com.bombombom.devs.external.global.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CircuitBreakerProvider {

    public static final String CIRCUIT_REDIS = "CB_REDIS";

    private final CircuitBreakerProperty circuitBreakerProperty;

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.ofDefaults();
    }

    @Bean
    public CircuitBreaker redisCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        return circuitBreakerRegistry.circuitBreaker(
            CIRCUIT_REDIS, CircuitBreakerConfig.custom()
                .failureRateThreshold(circuitBreakerProperty.failureRateThreshold)
                .slowCallDurationThreshold(
                    Duration.ofMillis(circuitBreakerProperty.slowCallDurationThreshold))
                .slowCallRateThreshold(circuitBreakerProperty.slowCallRateThreshold)
                .waitDurationInOpenState(
                    Duration.ofMillis(circuitBreakerProperty.waitDurationInOpenState))
                .minimumNumberOfCalls(circuitBreakerProperty.minimumNumberOfCalls)
                .slidingWindowSize(circuitBreakerProperty.slidingWindowSize)
                .permittedNumberOfCallsInHalfOpenState(
                    circuitBreakerProperty.permittedNumberOfCallsInHalfOpenState)
                .build()
        );
    }
}

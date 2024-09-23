package com.bombombom.devs.external.global.circuitbreaker.config;

import io.github.resilience4j.spring6.circuitbreaker.configure.CircuitBreakerConfigurationProperties;
import io.github.resilience4j.spring6.retry.configure.RetryConfigurationProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class Resilience4jOrderConfig {

    private static final int PRIORITY_1 = -3;
    private static final int PRIORITY_2 = -4;

    private final CircuitBreakerConfigurationProperties circuitBreakerConfigurationProperties;
    private final RetryConfigurationProperties retryConfigurationProperties;

    @PostConstruct
    public void setOrder() {
        circuitBreakerConfigurationProperties.setCircuitBreakerAspectOrder(PRIORITY_2);
        retryConfigurationProperties.setRetryAspectOrder(PRIORITY_1);
    }
}

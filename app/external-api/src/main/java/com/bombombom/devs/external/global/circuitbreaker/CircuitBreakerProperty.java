package com.bombombom.devs.external.global.circuitbreaker;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "resilience4j.circuitbreaker")
public class CircuitBreakerProperty {

    public Float failureRateThreshold = 50f;        // 실패율 임계 값 (퍼센트)
    public Float slowCallRateThreshold = 100f;      // 느린 호출 임계 값 (퍼센트)
    public Long slowCallDurationThreshold = 60000L; // 호출시간 임계 값
    public Long waitDurationInOpenState = 10000L;   // OPEN에서 HALF_OPEN으로 변경시키기 전까지 대기 시간
    public Integer minimumNumberOfCalls = 100;      // 서킷브레이커가 집계하기 전 최소 호출 횟수
    public Integer slidingWindowSize = 10;          // 서킷브레이커가 모니터링 할 요청 수 범위
    public Integer permittedNumberOfCallsInHalfOpenState = 10; // 서킷브레이커의 상태가 HALF_OPEN일 때 허용되는 호출 횟수
}

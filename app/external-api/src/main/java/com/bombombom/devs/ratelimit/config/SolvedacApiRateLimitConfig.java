package com.bombombom.devs.ratelimit.config;

import static com.bombombom.devs.job.AlgorithmStudyAssignmentJob.ALGORITHM_ASSIGNMENT_TRIGGER_KEY;

import com.bombombom.devs.algo.repository.AlgorithmAssignmentRateLimitRepository;
import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.core.exception.RateLimitException;
import com.bombombom.devs.job.AlgorithmStudyAssignmentJob;
import com.bombombom.devs.job.QuartzJobScheduler;
import com.bombombom.devs.ratelimit.ApiRateLimiter;
import io.github.bucket4j.BucketConfiguration;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.quartz.Trigger;
import org.springframework.stereotype.Component;

/**
 * 외부 API(solved.ac API)의 호출 제한 조건(256개의 request / window, window size = 15분)을 반영하기 위한, Token Bucket
 * 설정파일이다. Token을 리필하는 기준은, 7.5분마다 128개의 Token만 리필된다. (리필 경계 시점에서 순간적으로 많은 요청이 발생할 경우 제한 조건보다 많은 요청이
 * 처리될 수 있기 때문)</p>
 * <p>
 * {@link com.bombombom.devs.solvedac.SolvedacClient SolvedacClient(외부 API 호출 client)}를 통해 외부 API를
 * 호출하여 알고리즘 과제를 할당하거나 과제 해결 여부를 판단하게 되는데, 이때 Aspect가 적용되어, Token 수가 요청 횟수 이상일 때만 외부 API를 호출할 수 있다.
 * 만약 Token 수가 부족하다면, 추후 해당 요청을 처리하기 위해 대기열 큐를 확인하는 스케줄러를 재등록하고(Token Bucket의 Token이 갱신되는 시점에 실행),
 * RateLimitException을 던진다.
 * </p>
 *
 * @see SolvedacApiRateLimitConfig#beforeJobExecution()
 * @see SolvedacApiRateLimitConfig#beforeJobExecution(Map)
 * @see SolvedacApiRateLimitConfig#triggerQueueScheduler()
 * @see <a href="https://github.com/Team-BomBomBom/Server/pull/51">Feat: #BBB-120 알고리즘 과제 할당 및 해결 여부
 * 요청에 Rate Limit과 대기열 시스템 적용</a>
 * @see <a href="https://github.com/Team-BomBomBom/Server/pull/78">Refactor: #BBB-162 호출 초과 시점에만 대기열
 * 큐에 알고리즘 과제 관련 요청 파라미터 추가</a>
 */

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SolvedacApiRateLimitConfig {

    private static final int CAPACITY = 128;
    private static final int REFILL_AMOUNT = 128;
    private static final int REFILL_DURATION_OF_SECONDS = 450;
    private static final String SOLVEDAC_BUCKET_KEY = "bucket:solvedac";

    private final ApiRateLimiter apiRateLimiter;
    private final QuartzJobScheduler quartzJobScheduler;
    private final AlgorithmStudyAssignmentJob algorithmStudyAssignmentJob;
    private final AlgorithmAssignmentRateLimitRepository algorithmAssignmentRateLimitRepository;

    /**
     * 특정 유저의 알고리즘 과제 해결 여부를 갱신하기 위해, 외부 API(solved.ac API)를 호출하기 전 수행된다. Bucket에 Token이 1개 이상 존재하는
     * 경우 호출할 수 있다. Token을 소모할 수 없는 경우에는 {@link SolvedacApiRateLimitConfig#triggerQueueScheduler()}를
     * 호출하여 스케줄러를 재등록하고, RateLimitException을 던진다.
     */
    @Before("execution(* com.bombombom.devs.solvedac.SolvedacClient.checkProblemSolved(..))")
    public void beforeJobExecution() {
        if (!apiRateLimiter.tryConsume(SOLVEDAC_BUCKET_KEY, createBucketConfiguration())) {
            triggerQueueScheduler();
            throw new RateLimitException(SOLVEDAC_BUCKET_KEY);
        }
    }

    /**
     * 특정 알고리즘 스터디의 과제를 할당하기 위해, 외부 API(solved.ac API)를 호출하기 전 수행된다. Bucket에 Token이
     * {@code algorithmTagCount}개 이상 존재하는 경우 호출할 수 있다. Token을 소모할 수 없는 경우에는
     * {@link SolvedacApiRateLimitConfig#triggerQueueScheduler()}를 호출하여 스케줄러를 재등록하고,
     * RateLimitException을 던진다.
     *
     * @param problemCountForEachTag 알고리즘 분류와 해당 문제 개수를 매핑한 Map 객체
     */
    @Before("execution(* com.bombombom.devs.solvedac.SolvedacClient.getUnSolvedProblems(..)) && "
        + "args(.., problemCountForEachTag)")
    public void beforeJobExecution(Map<AlgoTag, Integer> problemCountForEachTag) {
        int algorithmTagCount = problemCountForEachTag.keySet().size();
        if (!apiRateLimiter.tryConsume(SOLVEDAC_BUCKET_KEY, createBucketConfiguration(),
            algorithmTagCount)) {
            triggerQueueScheduler();
            throw new RateLimitException(SOLVEDAC_BUCKET_KEY);
        }
    }

    private BucketConfiguration createBucketConfiguration() {
        return BucketConfiguration.builder().addLimit(limit -> limit.capacity(CAPACITY)
                .refillIntervally(REFILL_AMOUNT, Duration.ofSeconds(REFILL_DURATION_OF_SECONDS)))
            .build();
    }

    private int getTaskStatusUpdateDelayInSeconds() {
        LocalDateTime creationTime = algorithmAssignmentRateLimitRepository.getBucketCreationTime();
        long secondsDifference = Duration.between(creationTime, LocalDateTime.now())
            .getSeconds();
        long durationSeconds = secondsDifference % REFILL_DURATION_OF_SECONDS;
        return REFILL_DURATION_OF_SECONDS - (int) durationSeconds + 1;
    }

    /**
     * 외부 API 호출 초과 시점에 호출되는 메서드로, 대기열 큐에 존재하는 알고리즘 과제 관련 요청을 처리하기 위한 스케줄러를 등록하는 역할을 한다.
     * <p>
     * 해당 메서드는 호출 초과 시점에 항상 호출되기 때문에 이미 스케줄러가 재등록되었는지 확인한다. 이후
     * {@link com.bombombom.devs.job.AlgorithmStudyAssignmentJob}을 수행하는 스케줄러를 등록하게 되는데, Token
     * Bucket의 Token이 갱신 되는 시점에 동작하도록 구성한다.</p>
     */
    private void triggerQueueScheduler() {
        try {
            if (quartzJobScheduler.isTriggerAlreadyInitialized(ALGORITHM_ASSIGNMENT_TRIGGER_KEY)) {
                return;
            }
            Trigger initialTrigger = algorithmStudyAssignmentJob.buildJobTrigger(
                getTaskStatusUpdateDelayInSeconds());
            quartzJobScheduler.rescheduleJob(ALGORITHM_ASSIGNMENT_TRIGGER_KEY, initialTrigger);
        } catch (Exception e) {
            log.info(String.valueOf(e));
        }
    }
}

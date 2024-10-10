package com.bombombom.devs.ratelimit.config;

import com.bombombom.devs.algo.repository.AlgorithmProblemRedisRepository;
import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.core.exception.RateLimitException;
import com.bombombom.devs.job.AlgorithmStudyAssignmentJob;
import com.bombombom.devs.job.QuartzJobScheduler;
import com.bombombom.devs.ratelimit.ApiRateLimiter;
import io.github.bucket4j.BucketConfiguration;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

/**
 * 외부 API(solved.ac API)의 호출 제한 조건(256개의 request / window, window size = 15분)을 반영하기 위한, Token Bucket
 * 설정파일이다. Token을 리필하는 기준은, 7.5분마다 128개의 Token만 리필된다. (리필 경계 시점에서 순간적으로 많은 요청이 발생할 경우 제한 조건보다 많은 요청이
 * 처리될 수 있기 때문)</p>
 * <p>
 * {@link com.bombombom.devs.solvedac.SolvedacClient SolvedacClient(외부 API 호출 client)}를 통해 외부 API를
 * 호출하여 알고리즘 과제를 할당하거나 과제 해결 여부를 판단하게 되는데, 이때 Aspect가 적용되어, Token 수가 요청 횟수 이상일 때만 외부 API를 호출할 수
 * 있다.</p>
 *
 * @see SolvedacApiRateLimitConfig#beforeJobExecution()
 * @see SolvedacApiRateLimitConfig#beforeJobExecution(Map)
 * @see <a href="https://github.com/Team-BomBomBom/Server/pull/51">Feat: #BBB-120 알고리즘 과제 할당 및 해결 여부
 * 요청에 Rate Limit과 대기열 시스템 적용</a>
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
    private final AlgorithmProblemRedisRepository algorithmProblemRedisRepository;

    /**
     * 특정 유저의 알고리즘 과제 해결 여부를 갱신하기 위해, 외부 API(solved.ac API)를 호출하기 전 수행된다. Bucket에 Token이 1개 이상 존재하는
     * 경우 호출할 수 있고, Token이 존재하지 않는 경우에는 Token이 리필되기 전까지
     * {@link com.bombombom.devs.job.AlgorithmStudyAssignmentJob}을 수행하는 스케줄러의 동작이 일시정지된다.
     */
    @Before("execution(* com.bombombom.devs.solvedac.SolvedacClient.checkProblemSolved(..))")
    public void beforeJobExecution() throws SchedulerException {
        if (!apiRateLimiter.tryConsume(SOLVEDAC_BUCKET_KEY, createBucketConfiguration())) {
            TriggerKey triggerKey = algorithmStudyAssignmentJob.getTriggerKey();
            quartzJobScheduler.removeTrigger(triggerKey);
            JobDetail jobDetail = algorithmStudyAssignmentJob.getJobDetail();
            try {
                Trigger trigger = algorithmStudyAssignmentJob.buildJobTriggerAtTime(
                    getTaskStatusUpdateDelayInSeconds());
                quartzJobScheduler.setScheduleJob(jobDetail, trigger);
            } catch (NoSuchElementException e) {
                log.info(String.valueOf(e));
            }
            throw new RateLimitException(SOLVEDAC_BUCKET_KEY);
        }
    }

    /**
     * 특정 알고리즘 스터디의 과제를 할당하기 위해, 외부 API(solved.ac API)를 호출하기 전 수행된다. Bucket에 Token이
     * {@code algorithmTagCount}개 이상 존재하는 경우 호출할 수 있다.
     *
     * @param problemCountForEachTag 알고리즘 분류와 해당 문제 개수를 매핑한 Map 객체
     */
    @Before("execution(* com.bombombom.devs.solvedac.SolvedacClient.getUnSolvedProblems(..)) && "
        + "args(.., problemCountForEachTag)")
    public void beforeJobExecution(Map<AlgoTag, Integer> problemCountForEachTag) {
        int algorithmTagCount = problemCountForEachTag.keySet().size();
        if (!apiRateLimiter.tryConsume(SOLVEDAC_BUCKET_KEY, createBucketConfiguration(),
            algorithmTagCount)) {
            throw new RateLimitException(SOLVEDAC_BUCKET_KEY);
        }
    }

    private BucketConfiguration createBucketConfiguration() {
        return BucketConfiguration.builder().addLimit(limit -> limit.capacity(CAPACITY)
                .refillIntervally(REFILL_AMOUNT, Duration.ofSeconds(REFILL_DURATION_OF_SECONDS)))
            .build();
    }

    private int getTaskStatusUpdateDelayInSeconds() {
        LocalDateTime creationTime = algorithmProblemRedisRepository.getBucketCreationTime();
        long secondsDifference = Duration.between(creationTime, LocalDateTime.now())
            .getSeconds();
        long durationSeconds = secondsDifference % REFILL_DURATION_OF_SECONDS;
        return REFILL_DURATION_OF_SECONDS - (int) durationSeconds + 1;
    }
}

package com.bombombom.devs.ratelimit.config;

import com.bombombom.devs.algo.repository.AlgorithmProblemRedisRepository;
import com.bombombom.devs.core.exception.RateLimitException;
import com.bombombom.devs.job.QuartzJobScheduler;
import com.bombombom.devs.job.UpdateAlgorithmStudyTaskStatusJob;
import com.bombombom.devs.ratelimit.ApiRateLimiter;
import io.github.bucket4j.BucketConfiguration;
import java.time.Duration;
import java.time.LocalDateTime;
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
    private final UpdateAlgorithmStudyTaskStatusJob updateAlgorithmStudyTaskStatusJob;
    private final AlgorithmProblemRedisRepository algorithmProblemRedisRepository;

    @Before("execution(* com.bombombom.devs.solvedac.SolvedacClient.checkProblemSolved(..))")
    public void beforeJobExecution() throws SchedulerException {
        if (!apiRateLimiter.tryConsume(SOLVEDAC_BUCKET_KEY, this.createBucketConfiguration())) {
            TriggerKey triggerKey = updateAlgorithmStudyTaskStatusJob.getTriggerKey();
            quartzJobScheduler.removeTrigger(triggerKey);
            JobDetail jobDetail = updateAlgorithmStudyTaskStatusJob.getJobDetail();
            try {
                Trigger trigger = updateAlgorithmStudyTaskStatusJob.buildJobTriggerAtTime(
                    getTaskStatusUpdateDelayInSeconds());
                quartzJobScheduler.setScheduleJob(jobDetail, trigger);
            } catch (NoSuchElementException e) {
                log.info(String.valueOf(e));
            }
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

package com.bombombom.devs.job;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@DependsOn("algorithmStudyAssignmentJob")
public class QuartzJobScheduler {

    private final Scheduler scheduler;
    private final AlgorithmStudyAssignmentJob algorithmStudyAssignmentJob;

    @PostConstruct
    public void scheduleJob() {
        try {
            JobDetail jobDetail = RoundStartJob.buildJobDetail();
            Trigger trigger = RoundStartJob.buildJobTrigger();
            JobDetail algorithmAssignmentJobDetail = algorithmStudyAssignmentJob.getJobDetail();
            Trigger algorithmAssignmentTrigger = algorithmStudyAssignmentJob.buildJobTrigger();
            JobKey jobKey = jobDetail.getKey();
            if (!scheduler.checkExists(jobKey)) {
                scheduler.scheduleJob(jobDetail, trigger);
                log.info("Scheduled new job with key: {}", jobKey);
            } else {
                log.warn("Job already exists with key: {}", jobKey);
            }
            scheduler.scheduleJob(algorithmAssignmentJobDetail, algorithmAssignmentTrigger);
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }
    }

    public boolean isTriggerAlreadyInitialized(TriggerKey triggerKey) throws SchedulerException {
        Trigger trigger = scheduler.getTrigger(triggerKey);
        return scheduler.getTriggerState(triggerKey) == TriggerState.NORMAL
            && trigger.getPreviousFireTime() == null;
    }

    public void rescheduleJob(TriggerKey triggerKey, Trigger trigger) throws SchedulerException {
        scheduler.rescheduleJob(triggerKey, trigger);
    }
}

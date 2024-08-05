package com.bombombom.devs.job;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzJobScheduler {

    private final Scheduler scheduler;
    private final UpdateAlgorithmStudyTaskStatusJob updateAlgorithmStudyTaskStatusJob;

    @PostConstruct
    public void scheduleJob() {
        JobDetail jobDetail = RoundStartJob.buildJobDetail();
        Trigger trigger = RoundStartJob.buildJobTrigger();
        JobDetail updateAlgoStudyTaskStatusDetail = updateAlgorithmStudyTaskStatusJob.getJobDetail();
        Trigger updateAlgoStudyTaskStatusTrigger = updateAlgorithmStudyTaskStatusJob.getTrigger();
        try {
            JobKey jobKey = jobDetail.getKey();
            if (!scheduler.checkExists(jobKey)) {
                scheduler.scheduleJob(jobDetail, trigger);
                log.info("Scheduled new job with key: {}", jobKey);
            } else {
                log.warn("Job already exists with key: {}", jobKey);
            }
            scheduler.scheduleJob(updateAlgoStudyTaskStatusDetail,
                updateAlgoStudyTaskStatusTrigger);
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }
    }

    public void setScheduleJob(Trigger trigger) throws SchedulerException {
        scheduler.scheduleJob(trigger);
    }

    public void removeTrigger(String triggerName, String triggerGroup) throws SchedulerException {
        scheduler.unscheduleJob(new TriggerKey(triggerName, triggerGroup));
    }
}

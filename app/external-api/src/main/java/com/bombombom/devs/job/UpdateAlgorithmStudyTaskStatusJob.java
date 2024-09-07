package com.bombombom.devs.job;

import com.bombombom.devs.algo.model.vo.TaskStatusUpdateMessage;
import com.bombombom.devs.external.algo.service.AlgorithmProblemSolvedHistoryService;
import com.bombombom.devs.external.algo.service.dto.command.UpdateAlgorithmTaskStatusCommand;
import com.bombombom.devs.ratelimit.exception.RateLimitException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DateBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class UpdateAlgorithmStudyTaskStatusJob implements Job {

    private static final int SCHEDULER_INTERVAL_SECONDS = 2;
    private static final String UPDATE_ALGORITHM_TASK_STATUS_TRIGGER_NAME = "UPDATE_TASK_TRIGGER";
    private static final String UPDATE_ALGORITHM_TASK_STATUS_TRIGGER_GROUP = "UPDATE_TASK_TRIGGER_GROUP";

    private final AlgorithmProblemSolvedHistoryService algorithmProblemSolvedHistoryService;

    private Trigger trigger;
    private JobDetail jobDetail;
    private TriggerKey triggerKey;
    private SimpleScheduleBuilder scheduleBuilder;

    @PostConstruct
    public void init() {
        scheduleBuilder = buildSchedule();
        triggerKey = buildTriggerKey();
        trigger = buildJobTrigger();
        jobDetail = buildJobDetail();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            TaskStatusUpdateMessage pendingMessage = algorithmProblemSolvedHistoryService.getUnprocessedTaskStatusUpdateMessage();
            if (pendingMessage != null) {
                algorithmProblemSolvedHistoryService.updateTaskStatus(
                    UpdateAlgorithmTaskStatusCommand.fromMessage(pendingMessage));
                return;
            }
            TaskStatusUpdateMessage message = algorithmProblemSolvedHistoryService.getTaskStatusUpdateMessage();
            if (message == null) {
                return;
            }
            algorithmProblemSolvedHistoryService.updateTaskStatus(
                UpdateAlgorithmTaskStatusCommand.fromMessage(message));
        } catch (RateLimitException e) {
            log.info(e.getMessage());
        }
    }

    public Trigger buildJobTriggerAtTime(int delayInSeconds) {
        triggerKey = buildTriggerKey();
        return TriggerBuilder.newTrigger().withIdentity(triggerKey)
            .startAt(DateBuilder.futureDate(delayInSeconds, DateBuilder.IntervalUnit.SECOND))
            .withSchedule(scheduleBuilder)
            .forJob(jobDetail)
            .build();
    }

    private TriggerKey buildTriggerKey() {
        return new TriggerKey(UPDATE_ALGORITHM_TASK_STATUS_TRIGGER_NAME,
            UPDATE_ALGORITHM_TASK_STATUS_TRIGGER_GROUP);
    }

    private Trigger buildJobTrigger() {
        return TriggerBuilder.newTrigger().withIdentity(triggerKey)
            .withSchedule(scheduleBuilder)
            .build();
    }

    private JobDetail buildJobDetail() {
        return JobBuilder.newJob(UpdateAlgorithmStudyTaskStatusJob.class).build();
    }

    private SimpleScheduleBuilder buildSchedule() {
        return SimpleScheduleBuilder.simpleSchedule()
            .withIntervalInSeconds(SCHEDULER_INTERVAL_SECONDS).repeatForever();
    }
}

package com.bombombom.devs.job;

import com.bombombom.devs.external.algo.service.AlgorithmProblemSolvedHistoryService;
import com.bombombom.devs.external.algo.service.dto.command.UpdateAlgorithmTaskStatusCommand;
import com.bombombom.devs.study.repository.AlgorithmProblemSolvedHistoryRedisRepository;
import jakarta.annotation.PostConstruct;
import java.util.Map;
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

    private Trigger trigger;
    private JobDetail jobDetail;

    public static final String UPDATE_ALGORITHM_TASK_STATUS_TRIGGER_NAME = "UPDATE_TASK_TRIGGER";
    public static final String UPDATE_ALGORITHM_TASK_STATUS_TRIGGER_GROUP = "UPDATE_TASK_TRIGGER_GROUP";

    private final AlgorithmProblemSolvedHistoryService algorithmProblemSolvedHistoryService;
    private final AlgorithmProblemSolvedHistoryRedisRepository algorithmProblemSolveHistoryRedisRepository;

    @PostConstruct
    public void init() {
        trigger = buildJobTrigger();
        jobDetail = buildJobDetail();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        Map<String, String> message = algorithmProblemSolvedHistoryService.getTaskStatusUpdateMessage();
        if (message == null) {
            return;
        }
        algorithmProblemSolvedHistoryService.updateTaskStatus(
            UpdateAlgorithmTaskStatusCommand.fromMessage(message));
    }

    public TriggerKey getTriggerKey() {
        return new TriggerKey(UPDATE_ALGORITHM_TASK_STATUS_TRIGGER_NAME,
            UPDATE_ALGORITHM_TASK_STATUS_TRIGGER_GROUP);
    }

    public Trigger buildJobTriggerAtTime(int delayInSeconds) {
        return TriggerBuilder.newTrigger().withIdentity(getTriggerKey())
            .startAt(DateBuilder.futureDate(delayInSeconds, DateBuilder.IntervalUnit.SECOND))
            .forJob(buildJobDetail())
            .build();
    }

    private Trigger buildJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
            .withIntervalInSeconds(10).repeatForever();
        return TriggerBuilder.newTrigger().withIdentity(getTriggerKey())
            .withSchedule(scheduleBuilder)
            .build();
    }

    private JobDetail buildJobDetail() {
        return JobBuilder.newJob(UpdateAlgorithmStudyTaskStatusJob.class).build();
    }
}

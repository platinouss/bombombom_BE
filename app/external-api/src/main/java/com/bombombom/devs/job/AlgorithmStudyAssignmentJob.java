package com.bombombom.devs.job;

import com.bombombom.devs.algo.model.vo.AlgorithmProblemQueueMessage;
import com.bombombom.devs.external.algo.service.AlgorithmProblemQueueService;
import com.bombombom.devs.external.algo.service.dto.command.AssignAlgorithmProblemCommand;
import com.bombombom.devs.external.algo.service.dto.command.UpdateAlgorithmTaskStatusCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

/**
 * 해당 Job을 수행하는 스케줄러는, {@code SCHEDULER_INTERVAL_SECONDS} 간격으로 외부 API(solved.ac API) 호출 요청이 담긴 대기열
 * 큐를 확인하고, 요청을 순차적으로 처리한다.
 * <p>
 * {@link AlgorithmStudyAssignmentJob#execute(JobExecutionContext)} 메서드는 대기열 큐에 담긴 요청(또는 아직 처리되지 않은
 * 요청)을 확인한다. 처리할 수 있는 요청이 있는 경우,
 * {@link AlgorithmStudyAssignmentJob#executeMessage(AlgorithmProblemQueueMessage)} 메서드에서 메시지 타입(할당
 * 또는 갱신 요청)에 따라 적절한 메서드를 호출하여 로직을 수행한다. </p>
 *
 * @see <a href="https://github.com/Team-BomBomBom/Server/pull/51">Feat: #BBB-120 알고리즘 과제 할당 및 해결 여부
 * 요청에 Rate Limit과 대기열 시스템 적용</a>
 */

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class AlgorithmStudyAssignmentJob implements Job {

    private static final int SCHEDULER_INTERVAL_SECONDS = 2;
    private static final String ALGORITHM_ASSIGNMENT_TRIGGER_NAME = "ALGORITHM_ASSIGNMENT_TRIGGER";
    private static final String ALGORITHM_ASSIGNMENT_TRIGGER_GROUP = "ALGORITHM_ASSIGNMENT_TRIGGER_GROUP";

    private final ObjectMapper objectMapper;
    private final AlgorithmProblemQueueService algorithmProblemQueueService;

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
            AlgorithmProblemQueueMessage message = algorithmProblemQueueService.getUnprocessedAssignOrTaskStatusUpdateMessage();
            if (message == null) {
                message = algorithmProblemQueueService.getAssignOrTaskStatusUpdateMessage();
            }
            if (message != null) {
                executeMessage(message);
            }
        } catch (Exception e) {
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

    private void executeMessage(AlgorithmProblemQueueMessage message)
        throws JsonProcessingException {
        switch (message.requestType()) {
            case ASSIGN -> algorithmProblemQueueService.assignProblems(
                AssignAlgorithmProblemCommand.fromMessage(message, objectMapper));
            case UPDATE -> algorithmProblemQueueService.updateTaskStatus(
                UpdateAlgorithmTaskStatusCommand.fromMessage(message, objectMapper));
        }
    }

    private TriggerKey buildTriggerKey() {
        return new TriggerKey(ALGORITHM_ASSIGNMENT_TRIGGER_NAME,
            ALGORITHM_ASSIGNMENT_TRIGGER_GROUP);
    }

    private Trigger buildJobTrigger() {
        return TriggerBuilder.newTrigger().withIdentity(triggerKey)
            .withSchedule(scheduleBuilder)
            .build();
    }

    private JobDetail buildJobDetail() {
        return JobBuilder.newJob(AlgorithmStudyAssignmentJob.class).build();
    }

    private SimpleScheduleBuilder buildSchedule() {
        return SimpleScheduleBuilder.simpleSchedule()
            .withIntervalInSeconds(SCHEDULER_INTERVAL_SECONDS).repeatForever();
    }
}

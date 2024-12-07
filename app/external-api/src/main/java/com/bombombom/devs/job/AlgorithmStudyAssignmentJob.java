package com.bombombom.devs.job;

import com.bombombom.devs.algo.model.vo.AlgorithmAssignmentQueueMessage;
import com.bombombom.devs.algo.model.vo.AssignAlgorithmProblemMessage;
import com.bombombom.devs.algo.model.vo.UpdateAlgorithmTaskStatusMessage;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.core.exception.RateLimitException;
import com.bombombom.devs.external.algo.service.AlgorithmProblemQueueService;
import com.bombombom.devs.external.algo.service.dto.command.AddSolvedProblemHistoriesCommand;
import com.bombombom.devs.external.algo.service.dto.command.AssignAlgorithmProblemCommand;
import com.bombombom.devs.external.study.service.AlgorithmStudyService;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.repository.RoundRepository;
import com.bombombom.devs.study.repository.StudyRepository;
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
import org.quartz.Scheduler;
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
 * {@link
 * AlgorithmStudyAssignmentJob#processAlgorithmAssignmentRequest(AlgorithmAssignmentQueueMessage)}
 * 메서드에서 메시지 타입(할당 또는 갱신 요청)에 따라 적절한 메서드를 호출하여 로직을 수행한다. </p>
 *
 * @see <a href="https://github.com/Team-BomBomBom/Server/pull/51">Feat: #BBB-120 알고리즘 과제 할당 및 해결 여부
 * 요청에 Rate Limit과 대기열 시스템 적용</a>
 */

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class AlgorithmStudyAssignmentJob implements Job {

    public static final TriggerKey ALGORITHM_ASSIGNMENT_TRIGGER_KEY = buildTriggerKey();
    private static final int SCHEDULER_INTERVAL_SECONDS = 2;
    private static final String ALGORITHM_ASSIGNMENT_TRIGGER_NAME = "ALGORITHM_ASSIGNMENT_TRIGGER";
    private static final String ALGORITHM_ASSIGNMENT_TRIGGER_GROUP = "ALGORITHM_ASSIGNMENT_TRIGGER_GROUP";

    private final Scheduler scheduler;
    private final ObjectMapper objectMapper;
    private final AlgorithmStudyService algorithmStudyService;
    private final AlgorithmProblemQueueService algorithmProblemQueueService;
    private final StudyRepository studyRepository;
    private final RoundRepository roundRepository;

    private JobDetail jobDetail;
    private SimpleScheduleBuilder scheduleBuilder;

    @PostConstruct
    public void init() {
        scheduleBuilder = buildSchedule();
        jobDetail = buildJobDetail();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            AlgorithmAssignmentQueueMessage message = algorithmProblemQueueService.getUnprocessedAssignOrTaskStatusUpdateMessage();
            if (message == null) {
                message = algorithmProblemQueueService.getAssignOrTaskStatusUpdateMessage();
            }
            if (message == null) {
                scheduler.pauseTrigger(ALGORITHM_ASSIGNMENT_TRIGGER_KEY);
                return;
            }
            processAlgorithmAssignmentRequest(message);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    public Trigger buildJobTrigger() {
        return TriggerBuilder.newTrigger().withIdentity(ALGORITHM_ASSIGNMENT_TRIGGER_KEY)
            .withSchedule(scheduleBuilder)
            .build();
    }

    public Trigger buildJobTrigger(int delayInSeconds) {
        return TriggerBuilder.newTrigger().withIdentity(ALGORITHM_ASSIGNMENT_TRIGGER_KEY)
            .startAt(DateBuilder.futureDate(delayInSeconds, DateBuilder.IntervalUnit.SECOND))
            .withSchedule(scheduleBuilder)
            .forJob(jobDetail)
            .build();
    }

    private void processAlgorithmAssignmentRequest(AlgorithmAssignmentQueueMessage record)
        throws JsonProcessingException {
        switch (record.requestType()) {
            case ASSIGN -> assignAlgorithmProblems(record);
            case UPDATE -> updateAlgorithmTaskStatus(record);
        }
    }

    private void assignAlgorithmProblems(AlgorithmAssignmentQueueMessage record)
        throws JsonProcessingException {
        AssignAlgorithmProblemMessage message = AssignAlgorithmProblemMessage.fromRecord(
            record, objectMapper);
        Round round = roundRepository.findById(message.roundId())
            .orElseThrow(() -> new NotFoundException(ErrorCode.ROUND_NOT_FOUND));
        try {
            algorithmStudyService.assignAlgorithmProblems(
                AssignAlgorithmProblemCommand.fromMessage(message, round));
            algorithmProblemQueueService.completeAssignProblems(record.recordId());
        } catch (NotFoundException e) {
            algorithmProblemQueueService.completeAssignProblems(record.recordId());
        } catch (RateLimitException e) {
            log.info("assign algorithm problem rate exceeded");
        }
    }

    private void updateAlgorithmTaskStatus(AlgorithmAssignmentQueueMessage record)
        throws JsonProcessingException {
        UpdateAlgorithmTaskStatusMessage message = UpdateAlgorithmTaskStatusMessage.fromRecord(
            record, objectMapper);
        try {
            algorithmStudyService.addSolvedProblemHistories(
                AddSolvedProblemHistoriesCommand.fromMessage(message));
            algorithmProblemQueueService.completeUpdateTaskStatus(message.studyId(),
                message.userId(), record.recordId());
        } catch (NotFoundException e) {
            algorithmProblemQueueService.completeUpdateTaskStatus(message.studyId(),
                message.userId(), record.recordId());
        } catch (RateLimitException e) {
            log.info("update algorithm assignment status rate exceeded");
        }
    }

    private static TriggerKey buildTriggerKey() {
        return new TriggerKey(ALGORITHM_ASSIGNMENT_TRIGGER_NAME,
            ALGORITHM_ASSIGNMENT_TRIGGER_GROUP);
    }

    private JobDetail buildJobDetail() {
        return JobBuilder.newJob(AlgorithmStudyAssignmentJob.class).build();
    }

    private SimpleScheduleBuilder buildSchedule() {
        return SimpleScheduleBuilder.simpleSchedule()
            .withIntervalInSeconds(SCHEDULER_INTERVAL_SECONDS).repeatForever();
    }
}

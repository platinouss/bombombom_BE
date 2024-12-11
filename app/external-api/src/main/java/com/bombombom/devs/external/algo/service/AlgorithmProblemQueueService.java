package com.bombombom.devs.external.algo.service;

import com.bombombom.devs.algo.model.vo.AlgorithmAssignmentQueueMessage;
import com.bombombom.devs.algo.model.vo.AlgorithmTaskUpdateStatus;
import com.bombombom.devs.algo.model.vo.AssignAlgorithmProblemMessage;
import com.bombombom.devs.algo.model.vo.PendingMessageInfo;
import com.bombombom.devs.algo.model.vo.UpdateAlgorithmTaskStatusMessage;
import com.bombombom.devs.algo.repository.AlgorithmProblemRedisQueueRepository;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.ServerInternalException;
import com.bombombom.devs.core.util.Clock;
import com.bombombom.devs.external.algo.service.dto.command.AssignAlgorithmProblemCommand;
import com.bombombom.devs.external.study.service.AlgorithmStudyService;
import com.bombombom.devs.external.study.service.dto.command.CheckAlgorithmProblemSolvedCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * '알고리즘 과제 할당 요청'과 '알고리즘 과제 해결 여부 갱신 요청' 처리 시 외부 API 호출 초과 시점이라면, 추후 해당 요청을 처리하기 위해 대기열 큐에 요청 파라미터를
 * 추가하는 {@link AlgorithmProblemQueueService#addAssignProblemRequest(AssignAlgorithmProblemMessage)}
 * 메서드와
 * {@link AlgorithmProblemQueueService#addUpdateTaskStatusRequest(UpdateAlgorithmTaskStatusMessage)}
 * 메서드가 호출된다.
 * <p>
 * 이후 {@link com.bombombom.devs.job.AlgorithmStudyAssignmentJob AlgorithmStudyAssignmentJob}을 수행하는
 * 스케줄러는 대기열 큐에 담긴 메시지를 순차적으로 읽고, 메시지 타입(과제 할당 또는 해결 여부 갱신 요청)에 따라 각각 실제 로직을 수행하는
 * {@link AlgorithmStudyService#assignAlgorithmProblems(AssignAlgorithmProblemCommand)}와
 * {@link AlgorithmStudyService#updateAlgorithmTaskStatus(CheckAlgorithmProblemSolvedCommand)}를
 * 호출하여, '알고리즘 과제 할당' 또는 '알고리즘 과제 해결 여부 갱신' 요청을 처리한다.</p>
 *
 * @see <a href="https://github.com/Team-BomBomBom/Server/pull/51">Feat: #BBB-120 알고리즘 과제 할당 및 해결 여부
 * 요청에 Rate Limit과 대기열 시스템 적용</a>
 * @see <a href="https://github.com/Team-BomBomBom/Server/pull/78">Refactor: #BBB-162 호출 초과 시점에만 대기열
 * 큐에 알고리즘 과제 관련 요청 파라미터 추가</a>
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AlgorithmProblemQueueService {

    static final long UPDATE_INTERVAL_MS = 5 * 60 * 1000;
    static final long PENDING_MESSAGE_PROCESSING_INTERVAL_MS = 60 * 1000;

    private final Clock clock;
    private final AlgorithmProblemRedisQueueRepository algorithmProblemRedisQueueRepository;

    @PostConstruct
    void init() {
        algorithmProblemRedisQueueRepository.createConsumerGroup();
    }

    @Transactional
    public void addUpdateTaskStatusRequest(UpdateAlgorithmTaskStatusMessage message) {
        try {
            algorithmProblemRedisQueueRepository.addMessage(message);
        } catch (JsonProcessingException e) {
            throw new ServerInternalException(ErrorCode.JSON_CONVERSION_FAIL);
        }
    }

    @Transactional
    public void addAssignProblemRequest(AssignAlgorithmProblemMessage message) {
        try {
            algorithmProblemRedisQueueRepository.addMessage(message);
        } catch (JsonProcessingException e) {
            throw new ServerInternalException(ErrorCode.JSON_CONVERSION_FAIL);
        }
    }

    @Transactional
    public void completeAssignProblems(String recordId) {
        algorithmProblemRedisQueueRepository.ackMessage(recordId);
    }

    @Transactional
    public void completeUpdateTaskStatus(Long studyId, Long userId, String recordId) {
        algorithmProblemRedisQueueRepository.setTaskUpdateCompleted(studyId, userId);
        algorithmProblemRedisQueueRepository.ackMessage(recordId);
    }

    public AlgorithmAssignmentQueueMessage getAssignOrTaskStatusUpdateMessage() {
        return algorithmProblemRedisQueueRepository.readMessage();
    }

    public AlgorithmAssignmentQueueMessage getUnprocessedAssignOrTaskStatusUpdateMessage() {
        PendingMessageInfo pendingMessageInfo = algorithmProblemRedisQueueRepository.getOldestPendingMessageInfo();
        if (pendingMessageInfo == null || pendingMessageInfo.elapsedTime().toMillis()
            < PENDING_MESSAGE_PROCESSING_INTERVAL_MS) {
            return null;
        }
        return algorithmProblemRedisQueueRepository.getOldestPendingMessage(
            pendingMessageInfo);
    }

    public boolean hasRecentlyUpdatedTaskStatus(Long studyId, Long userId) {
        AlgorithmTaskUpdateStatus taskUpdateStatus = algorithmProblemRedisQueueRepository.getTaskUpdateStatus(
            studyId, userId);
        if (taskUpdateStatus == null || taskUpdateStatus.statusUpdatedAt() == null) {
            return false;
        }
        long currentTimeMillis = clock.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        return taskUpdateStatus.isUpdating()
            || (currentTimeMillis - taskUpdateStatus.statusUpdatedAt()) < UPDATE_INTERVAL_MS;
    }
}

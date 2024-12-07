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
import com.bombombom.devs.external.algo.service.dto.command.AddSolvedProblemHistoriesCommand;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * '알고리즘 과제 할당 요청'과 '알고리즘 과제 해결 여부 갱신 요청' 시, 각각
 * {@link AlgorithmProblemQueueService#addAssignProblemRequest(Study, AlgorithmStudy, Round, Map)}
 * 메서드와 {@link AlgorithmProblemQueueService#addUpdateTaskStatusRequest(User, List, Long)} 메서드가
 * 호출된다.
 * <p>
 * 이 두 가지 요청은, 외부 API(solved.ac API)의 호출 제한 조건(15분에 256회)으로 인해 Token Bucket 알고리즘 기반 Rate Limit이
 * 적용되며, 대기열 큐에 해당 요청을 추가한다.
 * ({@link com.bombombom.devs.ratelimit.config.SolvedacApiRateLimitConfig
 * SolvedacApiRateLimitConfig} 에서 설정된 수치를 바탕으로 Rate Limit이 적용)
 * <p>
 * {@link com.bombombom.devs.job.AlgorithmStudyAssignmentJob AlgorithmStudyAssignmentJob}을 수행하는
 * 스케줄러는 대기열 큐에 담긴 메시지를 순차적으로 읽고, 메시지 타입(과제 할당 또는 갱신 요청)에 따라 각각
 * {@link
 * AlgorithmProblemQueueService#assignProblems(com.bombombom.devs.external.algo.service.dto.command.AssignAlgorithmProblemMessage)}와
 * {@link AlgorithmProblemQueueService#updateTaskStatus(AddSolvedProblemHistoriesCommand)}를 호출하여, 과제
 * 할당 또는 해결 여부 갱신 요청 로직을 수행한다. 이 두 메서드는 Rate Limit을 관리하는 Bucket에 Token이 존재하는 경우에만 수행된다.
 * ({@link com.bombombom.devs.ratelimit.config.SolvedacApiRateLimitConfig
 * SolvedacApiRateLimitConfig} 내부에 Aspect가 선언되어 Token consume 로직 수행)</p>
 *
 * @see <a href="https://github.com/Team-BomBomBom/Server/pull/51">Feat: #BBB-120 알고리즘 과제 할당 및 해결 여부
 * 요청에 Rate Limit과 대기열 시스템 적용</a>
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

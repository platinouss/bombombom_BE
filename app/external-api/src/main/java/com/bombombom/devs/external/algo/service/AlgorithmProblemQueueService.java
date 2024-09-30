package com.bombombom.devs.external.algo.service;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.algo.model.vo.AlgorithmProblemQueueMessage;
import com.bombombom.devs.algo.model.vo.AlgorithmTaskUpdateStatus;
import com.bombombom.devs.algo.model.vo.AssignAlgorithmProblem;
import com.bombombom.devs.algo.model.vo.PendingMessageInfo;
import com.bombombom.devs.algo.repository.AlgorithmProblemRedisQueueRepository;
import com.bombombom.devs.algo.repository.AlgorithmProblemRepository;
import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.core.exception.BusinessRuleException;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.core.exception.ServerInternalException;
import com.bombombom.devs.core.util.Clock;
import com.bombombom.devs.external.algo.service.dto.command.AssignAlgorithmProblemCommand;
import com.bombombom.devs.external.algo.service.dto.command.UpdateAlgorithmTaskStatusCommand;
import com.bombombom.devs.job.AlgorithmProblemConverter;
import com.bombombom.devs.solvedac.SolvedacClient;
import com.bombombom.devs.solvedac.dto.ProblemListResponse;
import com.bombombom.devs.solvedac.dto.ProblemResponse;
import com.bombombom.devs.study.model.AlgorithmProblemAssignment;
import com.bombombom.devs.study.model.AlgorithmProblemSolvedHistory;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.repository.AlgorithmProblemAssignmentRepository;
import com.bombombom.devs.study.repository.AlgorithmProblemSolvedHistoryRepository;
import com.bombombom.devs.study.repository.RoundRepository;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlgorithmProblemQueueService {

    static final long UPDATE_INTERVAL_MS = 5 * 60 * 1000;
    static final long PENDING_MESSAGE_PROCESSING_INTERVAL_MS = 60 * 1000;

    private final Clock clock;
    private final SolvedacClient solvedacClient;
    private final AlgorithmProblemConverter algorithmProblemConverter;
    private final AlgorithmProblemService algorithmProblemService;
    private final UserRepository userRepository;
    private final RoundRepository roundRepository;
    private final AlgorithmProblemRepository algorithmProblemRepository;
    private final AlgorithmProblemRedisQueueRepository algorithmProblemRedisQueueRepository;
    private final AlgorithmProblemAssignmentRepository algorithmProblemAssignmentRepository;
    private final AlgorithmProblemSolvedHistoryRepository algorithmProblemSolvedHistoryRepository;

    @PostConstruct
    void init() {
        algorithmProblemRedisQueueRepository.createConsumerGroup();
    }

    @Transactional
    public void addUpdateTaskStatusRequest(User user, List<AlgorithmProblem> problems,
        Long studyId) {
        if (hasRecentlyUpdatedTaskStatus(studyId, user.getId())) {
            throw new BusinessRuleException(ErrorCode.ALGORITHM_TASK_STATUS_RECENTLY_UPDATED);
        }
        try {
            Set<Integer> problemRefIds = problems.stream().map(AlgorithmProblem::getRefId)
                .collect(Collectors.toSet());
            algorithmProblemRedisQueueRepository.addMessage(studyId, user.getId(),
                user.getBaekjoon(), problemRefIds);
            algorithmProblemRedisQueueRepository.setTaskUpdateInProgress(studyId,
                user.getId());
        } catch (JsonProcessingException e) {
            throw new ServerInternalException(ErrorCode.JSON_CONVERSION_FAIL);
        }
    }

    @Transactional
    public void addAssignProblemRequest(Study study, AlgorithmStudy algorithmStudy, Round round,
        Map<AlgoTag, Integer> problemCountForEachTag) {
        try {
            algorithmProblemRedisQueueRepository.addMessage(
                AssignAlgorithmProblem.of(study, algorithmStudy, round, problemCountForEachTag));
        } catch (JsonProcessingException e) {
            throw new ServerInternalException(ErrorCode.JSON_CONVERSION_FAIL);
        }
    }

    @Transactional
    public void updateTaskStatus(UpdateAlgorithmTaskStatusCommand command) {
        try {
            ProblemListResponse solvedProblems = solvedacClient.checkProblemSolved(
                command.baekjoonId(), command.problemRefIds());
            List<Integer> problemRefIds = solvedProblems.items().stream()
                .map(ProblemResponse::problemId).toList();
            User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
            List<AlgorithmProblem> problems = algorithmProblemRepository.findAllByRefId(
                problemRefIds);
            Set<Long> solvedProblemIds = algorithmProblemSolvedHistoryRepository.findByUserIdAndProblemIds(
                    user.getId(), problems.stream().map(AlgorithmProblem::getId).toList())
                .stream().map(history -> history.getProblem().getId()).collect(Collectors.toSet());
            List<AlgorithmProblemSolvedHistory> histories = problems.stream()
                .filter(problem -> !solvedProblemIds.contains(problem.getId()))
                .map(problem -> AlgorithmProblemSolvedHistory.createAlgorithmProblemSolvedHistory(
                    user, problem, command.requestTime())).toList();
            algorithmProblemSolvedHistoryRepository.saveAll(histories);
            algorithmProblemRedisQueueRepository.setTaskUpdateCompleted(command.studyId(),
                command.userId());
            algorithmProblemRedisQueueRepository.ackMessage(command.recordId());
        } catch (NotFoundException e) {
            algorithmProblemRedisQueueRepository.setTaskUpdateCompleted(command.studyId(),
                command.userId());
            algorithmProblemRedisQueueRepository.ackMessage(command.recordId());
        }
    }

    @Transactional
    public void assignProblems(AssignAlgorithmProblemCommand command) {
        try {
            Round round = roundRepository.findById(command.roundId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.ROUND_NOT_FOUND));
            ProblemListResponse problemListResponse = solvedacClient.getUnSolvedProblems(
                command.baekjoonIds(), command.difficultySpread(),
                command.problemCountForEachTag());
            List<AlgorithmProblem> problems = algorithmProblemConverter.convert(
                problemListResponse);
            List<AlgorithmProblem> foundOrSavedProblems = algorithmProblemService.findProblemsThenSaveWhenNotExist(
                problems);
            assignProblemToRound(round, foundOrSavedProblems);
            algorithmProblemRedisQueueRepository.ackMessage(command.recordId());
        } catch (NotFoundException e) {
            algorithmProblemRedisQueueRepository.ackMessage(command.recordId());
        }
    }

    @Transactional
    public void assignProblemToRound(Round round, List<AlgorithmProblem> problems) {
        List<AlgorithmProblemAssignment> assignments = new ArrayList<>();
        for (AlgorithmProblem problem : problems) {
            assignments.add(AlgorithmProblemAssignment.of(round, problem));
        }
        algorithmProblemAssignmentRepository.saveAll(assignments);
    }

    public AlgorithmProblemQueueMessage getAssignOrTaskStatusUpdateMessage() {
        return algorithmProblemRedisQueueRepository.readMessage();
    }

    public AlgorithmProblemQueueMessage getUnprocessedAssignOrTaskStatusUpdateMessage() {
        PendingMessageInfo pendingMessageInfo = algorithmProblemRedisQueueRepository.getOldestPendingMessageInfo();
        if (pendingMessageInfo == null || pendingMessageInfo.elapsedTime().toMillis()
            < PENDING_MESSAGE_PROCESSING_INTERVAL_MS) {
            return null;
        }
        return algorithmProblemRedisQueueRepository.getOldestPendingMessage(
            pendingMessageInfo);
    }

    private boolean hasRecentlyUpdatedTaskStatus(Long studyId, Long userId) {
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

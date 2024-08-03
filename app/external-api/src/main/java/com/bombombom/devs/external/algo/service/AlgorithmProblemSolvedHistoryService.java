package com.bombombom.devs.external.algo.service;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.algo.repository.AlgorithmProblemRepository;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.external.algo.service.dto.command.UpdateAlgorithmTaskStatusCommand;
import com.bombombom.devs.solvedac.SolvedacClient;
import com.bombombom.devs.solvedac.dto.ProblemListResponse;
import com.bombombom.devs.solvedac.dto.ProblemResponse;
import com.bombombom.devs.study.model.AlgorithmProblemSolvedHistory;
import com.bombombom.devs.study.repository.AlgorithmProblemSolvedHistoryRedisRepository;
import com.bombombom.devs.study.repository.AlgorithmProblemSolvedHistoryRepository;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlgorithmProblemSolvedHistoryService {

    static final long UPDATE_INTERVAL_MS = 5 * 60 * 1000;

    private final UserRepository userRepository;
    private final SolvedacClient solvedacClient;
    private final AlgorithmProblemRepository algorithmProblemRepository;
    private final AlgorithmProblemSolvedHistoryRepository algorithmProblemSolvedHistoryRepository;
    private final AlgorithmProblemSolvedHistoryRedisRepository algorithmProblemSolvedHistoryRedisRepository;

    @PostConstruct
    void init() {
        algorithmProblemSolvedHistoryRedisRepository.createConsumerGroup();
    }

    @Transactional
    public void addUpdateTaskStatusRequest(User user, List<AlgorithmProblem> problems) {
        if (hasRecentlyUpdatedTaskStatus(user.getId())) {
            throw new IllegalStateException("Algorithm task status has been recently updated");
        }
        List<Integer> problemRefIds = problems.stream().map(AlgorithmProblem::getRefId).toList();
        algorithmProblemSolvedHistoryRedisRepository.addMessage(user.getId(), user.getBaekjoon(),
            problemRefIds);
        algorithmProblemSolvedHistoryRedisRepository.updateLastTaskUpdateTime(user.getId());
    }

    @Transactional
    public void updateTaskStatus(UpdateAlgorithmTaskStatusCommand command) {
        ProblemListResponse solvedProblems = solvedacClient.checkProblemSolved(
            command.baekjoonId(), command.problemRefIds());
        List<Integer> problemRefIds = solvedProblems.items().stream()
            .map(ProblemResponse::problemId).toList();
        User user = userRepository.findById(command.userId())
            .orElseThrow(() -> new NotFoundException("User Not Found"));
        List<AlgorithmProblem> problems = algorithmProblemRepository.findAllByRefId(problemRefIds);
        List<AlgorithmProblemSolvedHistory> histories = problems.stream().map(problem ->
            AlgorithmProblemSolvedHistory.createAlgorithmProblemSolvedHistory(user, problem,
                command.requestTime())).toList();
        algorithmProblemSolvedHistoryRepository.saveAll(histories);
        algorithmProblemSolvedHistoryRedisRepository.ackMessage(command.recordId());
    }

    public Map<String, String> getTaskStatusUpdateMessage() {
        return algorithmProblemSolvedHistoryRedisRepository.readMessage();
    }

    private boolean hasRecentlyUpdatedTaskStatus(Long userId) {
        long currentTimeMillis = Instant.now().toEpochMilli();
        String lastUpdateTime = algorithmProblemSolvedHistoryRedisRepository.getLastTaskUpdateTime(
            userId);
        if (lastUpdateTime == null) {
            return false;
        }
        long lastUpdateTimeMillis = Long.parseLong(lastUpdateTime);
        return (currentTimeMillis - lastUpdateTimeMillis) > UPDATE_INTERVAL_MS;
    }
}

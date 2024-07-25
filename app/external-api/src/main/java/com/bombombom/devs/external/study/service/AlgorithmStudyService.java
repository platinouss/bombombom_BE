package com.bombombom.devs.external.study.service;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.external.study.service.dto.result.progress.AlgorithmStudyProgress;
import com.bombombom.devs.study.model.AlgorithmProblemAssignment;
import com.bombombom.devs.study.model.AlgorithmProblemSolveHistory;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.StudyType;
import com.bombombom.devs.study.repository.AlgorithmProblemAssignmentRepository;
import com.bombombom.devs.study.repository.AlgorithmProblemSolveHistoryRepository;
import com.bombombom.devs.user.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlgorithmStudyService implements StudyProgressService {

    private final AlgorithmProblemAssignmentRepository algorithmProblemAssignmentRepository;
    private final AlgorithmProblemSolveHistoryRepository algorithmProblemSolveHistoryRepository;

    @Override
    public StudyType getStudyType() {
        return StudyType.ALGORITHM;
    }

    @Override
    public AlgorithmStudyProgress findStudyProgress(Round round, List<User> members) {
        List<Long> membersId = members.stream().map(User::getId).toList();
        List<AlgorithmProblem> problems = algorithmProblemAssignmentRepository.findProblemWithStudyByRound(
            round.getId()).stream().map(AlgorithmProblemAssignment::getProblem).toList();
        List<Long> problemsId = problems.stream().map(AlgorithmProblem::getId).toList();
        List<AlgorithmProblemSolveHistory> histories = algorithmProblemSolveHistoryRepository
            .findSolvedHistoryWithUserAndProblem(membersId, problemsId);
        return AlgorithmStudyProgress.fromEntity(round, problems, histories);
    }
}

// @formatter:off

package com.bombombom.devs.external.algo.service;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.algo.model.AlgorithmProblemFeedback;
import com.bombombom.devs.algo.repository.AlgorithmProblemFeedbackRepository;
import com.bombombom.devs.algo.repository.AlgorithmProblemRepository;
import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.core.exception.NotAcceptableException;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.core.util.Clock;
import com.bombombom.devs.external.algo.config.ProbabilityConfig;
import com.bombombom.devs.external.algo.service.dto.command.FeedbackAlgorithmProblemCommand;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.model.StudyType;
import com.bombombom.devs.study.repository.AlgorithmProblemAssignmentRepository;
import com.bombombom.devs.study.repository.RoundRepository;
import com.bombombom.devs.study.repository.StudyRepository;
import com.bombombom.devs.study.repository.UserStudyRepository;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AlgorithmProblemService {

    private final Clock clock;
    private final RandomGenerator randomGenerator;
    private final AlgorithmProblemRepository algorithmProblemRepository;
    private final RoundRepository roundRepository;
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final UserStudyRepository userStudyRepository;
    private final AlgorithmProblemAssignmentRepository algorithmProblemAssignmentRepository;
    private final AlgorithmProblemFeedbackRepository algorithmProblemFeedbackRepository;


    public void saveProblems(List<AlgorithmProblem> problems) {
        algorithmProblemRepository.saveAll(problems);
    }

    /**
     * totalProblemCount 개의 문제를 각 태그별로 정해진 확률대로 추첨하여 분배합니다.
     * 0. 각 태그의 문제 개수에 비례해서 해당 태그가 뽑힐 확률을 계산하여 환경변수로 설정한다.
     *    UX 상으로는 백준에 실제로 존재하는 문제 개수에 비례하는 것보다
     *    실전에서 출제되는 태그의 비율로 계속 업데이트 하는게 좋을 것 같다.
     * 1. 각 태그는 당첨 스프레드를 갖는다.
     *    예를 들어, DP 태그의 당첨 스프레드가 0.0 ~ 0.3 이라면
     *    생성된 난수가 0.0~0.3 사이에 위치할 때 해당 태그가 선택된다.
     * 2. 당첨 스프레드는 누적합을 통해서 계산한다.
     *    누적합은 0부터 시작하여 해당 태그의 확률을 더해가며 계산한다.
     *    태그의 당첨 스프레드는 {해당 태그의 확률을 더하기 전의 누적합}부터
     *    {해당 태그의 확률을 더한 후의 누적합} 사이로 정해진다.
     *    예를 들어, 당첨 확률을 더하기 전의 누적합이 0, DP의 당첨 확률은 0.3일 때
     *    DP의 당첨 스프레드는 0.0 ~ 0.3 사이로 정해진다.
     * 3. 랜덤 난수를 생성하여 각 태그의 당첨 스프레드와 비교하여 당첨 태그를 정한다.
     * 4. totalProblemCount 만큼 (1)~(3)을 반복하여 태그마다 문제 개수(problemCountByTag)를 카운트한다.
     * 5. problemCountByTag를 반환한다.
     *
     * @param totalProblemCount 총 문제 개수
     * @return problemCountByTag 태그별 문제 개수
     * @author 송승훈
     *
     */
    // 함수 인자로 선택할 Tag, 혹은 제외할 Tag를 받는 것도 고려해봤지만
    // 오버엔지니어링이 될 수 있으므로 최소 스펙으로 구현하였습니다

    public Map<AlgoTag, Integer> getProblemCountForEachTag(Integer totalProblemCount) {
        Map<AlgoTag, Integer> problemCountByTag = new HashMap<>();
        while (totalProblemCount-- > 0) {
            AlgoTag tag = drawProblem();
            problemCountByTag.merge(tag, 1, Integer::sum);
        }
        return problemCountByTag;
    }

    private AlgoTag drawProblem() {
        double rand = randomGenerator.nextDouble(ProbabilityConfig.totalProbability);
        return Arrays.stream(AlgoTag.values())
            .filter(algoTag -> algoTag.isInRange(rand))
            .findFirst()
            .orElse(null);
    }

    @Transactional
    public void feedback(Long userId,
        FeedbackAlgorithmProblemCommand feedbackAlgorithmProblemCommand) {


        Study study = studyRepository.findById(
                feedbackAlgorithmProblemCommand.studyId())
            .orElseThrow(() -> new NotFoundException("Study Not Found"));

        if (study.getStudyType() != StudyType.ALGORITHM) {
            throw new NotAcceptableException("Feedback can only be given to Algorithm Study");
        }

        AlgorithmStudy algorithmStudy = (AlgorithmStudy) study;

        Round round = roundRepository.findRoundByStudyIdAndStartDateBeforeAndEndDateAfter(
                algorithmStudy.getId(), clock.today())
            .orElseThrow(() -> new NotFoundException("Ongoing Round Not Found"));

        AlgorithmProblem problem = algorithmProblemRepository.findById(
                feedbackAlgorithmProblemCommand.problemId())
            .orElseThrow(() -> new NotFoundException("Problem Not Found"));

        if(!algorithmProblemAssignmentRepository.existsByRoundIdAndProblemId(round.getId(),
                problem.getId())){
            throw new NotAcceptableException("Problem is not ongoing assignment");

        }

        if (!userStudyRepository.existsByUserIdAndStudyId(userId,
            algorithmStudy.getId())) {
            throw new NotAcceptableException("User is not a member");
        }

        //TODO user가 problem에 해결했는지 solvehistory로 검증


        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User Not Found"));

            AlgorithmProblemFeedback preFeedback =
                algorithmProblemFeedbackRepository.findByUserIdAndProblemId(userId, problem.getId())
                    .orElse(null);


            AlgorithmProblemFeedback newFeedback = AlgorithmProblemFeedback.builder()
                .problem(problem)
                .user(user)
                .again(feedbackAlgorithmProblemCommand.again())
                .difficulty(feedbackAlgorithmProblemCommand.difficulty())
                .build();

            if(preFeedback != null){
                changeFeedback(algorithmStudy, preFeedback, newFeedback);
                preFeedback.update(newFeedback);

            } else{
                applyFeedback(algorithmStudy, newFeedback);
                problem.addFeedback(newFeedback);
                algorithmProblemRepository.save(problem);
            }

    }

    private void applyFeedback(AlgorithmStudy study, AlgorithmProblemFeedback feedback) {
        adjustDifficulty(study.getId(), feedback.getProblem().getTag(),
            study.getDifficultyVariance(feedback));
    }

    private void changeFeedback(AlgorithmStudy study, AlgorithmProblemFeedback preFeedback,
        AlgorithmProblemFeedback newFeedback) {
        adjustDifficulty(study.getId(), preFeedback.getProblem().getTag(),
            study.getDifficultyVariance(newFeedback) - study.getDifficultyVariance(preFeedback));
    }

    private void adjustDifficulty(Long studyId, AlgoTag tag, Float variance){

        switch (tag) {
            case AlgoTag.DP -> {
                studyRepository.increaseDifficultyDpById(studyId,variance);
            }
            case AlgoTag.GEOMETRY -> {
                studyRepository.increaseDifficultyGeometryById(studyId,variance);
            }
            case AlgoTag.DATA_STRUCTURES -> {
                studyRepository.increaseDifficultyDataStructureById(studyId,variance);
            }
            case AlgoTag.GRAPHS -> {
                studyRepository.increaseDifficultyGraphById(studyId,variance);
            }
            case AlgoTag.GREEDY -> {
                studyRepository.increaseDifficultyGreedyById(studyId, variance);
            }
            case AlgoTag.IMPLEMENTATION -> {
                studyRepository.increaseDifficultyImplementationById(studyId,variance);
            }
            case AlgoTag.MATH -> {
                studyRepository.increaseDifficultyMathById(studyId,variance);
            }
            case AlgoTag.STRING -> {
                studyRepository.increaseDifficultyStringById(studyId,variance);
            }
            default -> throw new IllegalStateException("Incorrect use of AlgoTag");
        }

    }

}

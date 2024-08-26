package com.bombombom.devs.external.algo.controller.dto.request;

import static com.bombombom.devs.algo.model.AlgorithmProblemFeedback.FeedbackDifficultyBegin;
import static com.bombombom.devs.algo.model.AlgorithmProblemFeedback.FeedbackDifficultyEnd;

import com.bombombom.devs.external.algo.service.dto.command.FeedbackAlgorithmProblemCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Range;

@Builder
public record FeedbackAlgorithmProblemRequest(
    @NotNull Long studyId,
    @NotNull Long problemId,
    @NotNull Boolean again,
    @Range(min = FeedbackDifficultyBegin, max = FeedbackDifficultyEnd) Integer difficulty
) {


    public FeedbackAlgorithmProblemCommand toServiceDto() {
        return FeedbackAlgorithmProblemCommand.builder()
            .studyId(studyId)
            .problemId(problemId)
            .again(again)
            .difficulty(difficulty)
            .build();

    }
}

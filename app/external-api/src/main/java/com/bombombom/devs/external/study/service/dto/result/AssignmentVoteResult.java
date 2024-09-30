package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.study.model.AssignmentVote;
import lombok.Builder;

@Builder
public record AssignmentVoteResult(
    RoundResult round,
    AssignmentResult first,
    AssignmentResult second

) {


    public static AssignmentVoteResult fromEntity(AssignmentVote vote) {
        AssignmentResult second = null;
        if (vote.getSecond() != null) {
            second = AssignmentResult.fromEntity(vote.getSecond());
        }

        return AssignmentVoteResult.builder()
            .round(RoundResult.fromEntity(vote.getRound()))
            .first(AssignmentResult.fromEntity(vote.getFirst()))
            .second(second)
            .build();
    }
}

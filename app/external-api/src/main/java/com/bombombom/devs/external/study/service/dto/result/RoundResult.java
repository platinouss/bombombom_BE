package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.study.model.Round;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record RoundResult(
    Integer idx,
    LocalDate startDate,
    LocalDate endDate
) {

    public static RoundResult fromEntity(Round round) {
        return RoundResult.builder()
            .idx(round.getIdx())
            .startDate(round.getStartDate())
            .endDate(round.getEndDate())
            .build();
    }

}

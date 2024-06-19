package com.bombombom.devs.study.service.dto.command;

import com.bombombom.devs.study.models.BookStudy;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record RegisterBookStudyCommand(
    @NotNull String name,
    @NotNull String introduce,
    Integer capacity,
    Integer weeks,
    @NotNull LocalDate startDate,
    Integer reliabilityLimit,
    Integer penalty,
    Long bookId
) {


}

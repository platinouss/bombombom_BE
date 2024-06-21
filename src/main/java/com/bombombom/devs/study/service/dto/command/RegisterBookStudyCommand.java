package com.bombombom.devs.study.service.dto.command;

import com.bombombom.devs.study.models.BookStudy;
import com.bombombom.devs.study.models.StudyStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record RegisterBookStudyCommand(
    String name,
    String introduce,
    Integer capacity,
    Integer weeks,
    LocalDate startDate,
    Integer reliabilityLimit,
    Integer penalty,
    StudyStatus state,
    Integer headCount,
    Long bookId
) implements RegisterStudyCommand {


}

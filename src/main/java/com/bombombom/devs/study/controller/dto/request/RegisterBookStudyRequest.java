package com.bombombom.devs.study.controller.dto.request;

import com.bombombom.devs.study.service.dto.command.RegisterBookStudyCommand;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record RegisterBookStudyRequest(
    @NotNull String name,
    @NotNull String introduce,
    int capacity,
    int weeks,
    @NotNull LocalDate startDate,
    int reliabilityLimit,
    int penalty,
    Long bookId
    ){
    public RegisterBookStudyCommand toServiceDto() {

        return RegisterBookStudyCommand.builder()
            .name(name).introduce(introduce)
            .capacity(capacity).weeks(weeks).startDate(startDate)
            .reliabilityLimit(reliabilityLimit).penalty(penalty)
            .bookId(bookId).build();

    }


}

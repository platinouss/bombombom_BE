package com.bombombom.devs.study.controller.dto.request;

import static com.bombombom.devs.study.Constants.MAX_CAPACITY;
import static com.bombombom.devs.study.Constants.MAX_PENALTY;
import static com.bombombom.devs.study.Constants.MAX_RELIABLITY_LIMIT;
import static com.bombombom.devs.study.Constants.MAX_WEEKS;

import com.bombombom.devs.study.service.dto.command.RegisterBookStudyCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;
import org.hibernate.validator.constraints.Range;

@Builder
public record RegisterBookStudyRequest(
    @NotBlank @Size(max = 255, message = "스터디명은 255자를 넘을 수 없습니다.") String name,
    @NotBlank @Size(max = 500, message = "스터디소개는 500자를 넘을 수 없습니다.") String introduce,
    @NotNull @Range(min = 1, max = MAX_CAPACITY) Integer capacity,
    @NotNull @Range(min = 1, max = MAX_WEEKS) Integer weeks,
    @NotNull LocalDate startDate,
    @NotNull @Range(max = MAX_RELIABLITY_LIMIT) Integer reliabilityLimit,
    @NotNull @Range(max = MAX_PENALTY) Integer penalty,
    @NotNull Long bookId
) {

    public RegisterBookStudyCommand toServiceDto() {

        return RegisterBookStudyCommand.builder()
            .name(name)
            .introduce(introduce)
            .capacity(capacity)
            .weeks(weeks)
            .startDate(startDate)
            .reliabilityLimit(reliabilityLimit)
            .penalty(penalty)
            .bookId(bookId)
            .build();

    }


}

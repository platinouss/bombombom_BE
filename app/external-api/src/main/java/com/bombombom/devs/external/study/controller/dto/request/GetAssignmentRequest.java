package com.bombombom.devs.external.study.controller.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record GetAssignmentRequest(
    @NotNull @Min(0) Integer roundIdx
) {

}

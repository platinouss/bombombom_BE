package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.study.model.Assignment;
import lombok.Builder;

@Builder
public record AssignmentResult(
    Long id,
    String title,
    String description,
    Integer pageStart,
    Integer pageEnd
) {

    public static AssignmentResult fromEntity(Assignment assignment) {

        return AssignmentResult.builder()
            .id(assignment.getId())
            .pageEnd(assignment.getPageEnd())
            .pageStart(assignment.getPageStart())
            .description(assignment.getDescription())
            .title(assignment.getTitle())
            .build();

    }


}

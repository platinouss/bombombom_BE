package com.bombombom.devs.external.study.controller.dto.request;

import com.bombombom.devs.external.study.service.dto.command.EditAssignmentCommand;
import com.bombombom.devs.external.study.service.dto.result.AssignmentResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

@Builder
public record EditAssignmentRequest(
    @NotNull @Min(0) Integer roundIdx,
    @Valid @Size(max = 20) List<AssignmentInfo> assignments
) {

    @Builder
    public record AssignmentInfo(
        @NotNull @Min(1) Long id,
        @NotBlank @Size(max = 100, message = "과제명은 100자를 넘길 수 없습니다.") String title,
        @Size(max = 500, message = "과제설명은 500자를 넘길 수 없습니다.") String description,
        @Min(0) Integer pageStart,
        @Min(0) Integer pageEnd
    ) {

        public static AssignmentInfo fromResult(AssignmentResult assignmentResult) {
            return AssignmentInfo.builder()
                .id(assignmentResult.id())
                .title(assignmentResult.title())
                .description(assignmentResult.description())
                .pageStart(assignmentResult.pageStart())
                .pageEnd(assignmentResult.pageEnd())
                .build();

        }

    }

    public EditAssignmentCommand toServiceDto() {
        return EditAssignmentCommand.builder()
            .roundIdx(roundIdx)
            .assignments(assignments)
            .build();

    }


}
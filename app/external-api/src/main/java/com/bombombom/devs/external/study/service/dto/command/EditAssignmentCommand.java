package com.bombombom.devs.external.study.service.dto.command;

import com.bombombom.devs.external.study.controller.dto.request.EditAssignmentRequest.AssignmentInfo;
import java.util.List;
import lombok.Builder;

@Builder
public record EditAssignmentCommand(

    Integer roundIdx,
    List<AssignmentInfo> assignments) {


}

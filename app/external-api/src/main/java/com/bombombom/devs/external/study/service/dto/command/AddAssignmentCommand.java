package com.bombombom.devs.external.study.service.dto.command;

import com.bombombom.devs.external.study.controller.dto.request.AddAssignmentRequest.NewAssignmentInfo;
import java.util.List;
import lombok.Builder;

@Builder
public record AddAssignmentCommand(

    Integer roundIdx,
    List<NewAssignmentInfo> assignments) {


}

package com.bombombom.devs.study.controller.dto.request;

import com.bombombom.devs.study.service.dto.command.RegisterBookStudyCommand;
import java.util.Optional;

public record SearchStudyRequest(

    int offset,
    int limit
) {


}

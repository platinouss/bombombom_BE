package com.bombombom.devs.study.controller.dto.response;


import java.util.List;
import lombok.Builder;

@Builder
public record StudyPageResponse(

    Long totalElements,
    Integer totalPages,
    Integer pageNumber,
    List<StudyResponse> contents
) {

}

package com.bombombom.devs.book.controller.dto;

import com.bombombom.devs.book.service.dto.SearchBookQuery;
import jakarta.validation.constraints.NotBlank;

public record BookListRequest(
    @NotBlank String keyword
) {

    public SearchBookQuery toServiceDto() {
        return SearchBookQuery.builder()
            .keyword(keyword)
            .build();
    }
}

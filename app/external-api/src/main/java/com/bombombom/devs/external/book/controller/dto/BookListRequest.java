package com.bombombom.devs.external.book.controller.dto;

import com.bombombom.devs.external.book.enums.SearchOption;
import com.bombombom.devs.external.book.service.dto.SearchBookQuery;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record BookListRequest(
    @NotBlank(message = "검색 키워드를 입력해 주세요.") String keyword,
    SearchOption searchOption
) {

    public SearchBookQuery toServiceDto() {
        return SearchBookQuery.builder()
            .keyword(keyword)
            .searchOption(searchOption)
            .build();
    }
}

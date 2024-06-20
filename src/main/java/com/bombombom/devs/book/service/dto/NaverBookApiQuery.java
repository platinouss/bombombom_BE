package com.bombombom.devs.book.service.dto;

import com.bombombom.devs.book.naverapi.enums.SortType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NaverBookApiQuery(
    @NotBlank String query,                 // 검색어
    @Size(min = 1, max = 100) int display,  // 가져올 검색 결과 개수
    @Size(min = 1, max = 100) int start,    // 검색 시작 위치
    SortType sort
) {

    public NaverBookApiQuery(String query) {
        this(query, 10, 1, SortType.SIM);
    }
}

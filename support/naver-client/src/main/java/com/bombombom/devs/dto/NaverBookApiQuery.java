package com.bombombom.devs.dto;

import com.bombombom.devs.enums.SortType;

public record NaverBookApiQuery(
    String query,                 // 검색어
    int display,  // 가져올 검색 결과 개수
    int start,    // 검색 시작 위치
    SortType sort
) {

    public NaverBookApiQuery(String query) {
        this(query, 30, 1, SortType.SIMILARITY);
    }
}

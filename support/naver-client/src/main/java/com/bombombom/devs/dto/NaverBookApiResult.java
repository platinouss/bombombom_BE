package com.bombombom.devs.dto;

import java.util.Date;
import java.util.List;
import lombok.Builder;

@Builder
public record NaverBookApiResult(
    Date lastBuildDate,
    int total,
    int start,
    int display,
    List<SearchBookItem> items
) {

    @Builder
    public record SearchBookItem(
        String title,
        String link,
        String image,
        String author,
        int discount,
        String publisher,
        Long isbn,
        String description,
        String pubdate
    ) {

    }
}

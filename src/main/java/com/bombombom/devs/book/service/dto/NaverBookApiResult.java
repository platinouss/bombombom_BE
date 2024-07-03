package com.bombombom.devs.book.service.dto;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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

    public List<IndexBookCommand> toServiceDto() {
        return items().stream()
            .filter(item -> Objects.nonNull(item.isbn))
            .map(item -> IndexBookCommand.builder()
                .title(item.title)
                .author(item.author)
                .publisher(item.publisher)
                .isbn(item.isbn)
                .imageUrl(item.image)
                .build())
            .collect(Collectors.toList());
    }
}

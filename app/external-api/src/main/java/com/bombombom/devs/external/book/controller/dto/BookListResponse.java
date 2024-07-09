package com.bombombom.devs.external.book.controller.dto;

import com.bombombom.devs.external.book.service.dto.SearchBooksResult;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record BookListResponse(
    List<BookInfo> booksInfo
) {

    public static BookListResponse fromResult(SearchBooksResult searchBooksResult) {
        return BookListResponse.builder()
            .booksInfo(searchBooksResult.bookResults().stream()
                .filter(bookResult -> Objects.nonNull(bookResult.isbn()))
                .map(BookInfo::fromResult)
                .collect(Collectors.toList()))
            .build();
    }
}

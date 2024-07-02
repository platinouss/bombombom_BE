package com.bombombom.devs.book.controller.dto;

import com.bombombom.devs.book.service.dto.NaverBookApiResult;
import com.bombombom.devs.book.service.dto.SearchBooksResult;
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
            .booksInfo(searchBooksResult.booksResult().stream()
                .filter(bookResult -> Objects.nonNull(bookResult.isbn()))
                .map(BookInfo::fromResult)
                .collect(Collectors.toList()))
            .build();
    }

    public static BookListResponse fromResult(NaverBookApiResult naverBookApiResult) {
        return BookListResponse.builder()
            .booksInfo(naverBookApiResult.items().stream()
                .filter(bookResult -> Objects.nonNull(bookResult.isbn()))
                .map(BookInfo::fromResult)
                .collect(Collectors.toList()))
            .build();
    }
}

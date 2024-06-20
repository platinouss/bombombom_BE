package com.bombombom.devs.book.controller.dto;

import com.bombombom.devs.book.service.dto.NaverBookApiResult;
import com.bombombom.devs.book.service.dto.SearchBooksResult;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record BookListResponse(
    List<BookInfo> booksInfo
) {

    @Builder
    record BookInfo(
        String title,
        String author,
        String publisher,
        Long isbn,
        String tableOfContents
    ) {

    }

    public static BookListResponse fromResult(SearchBooksResult searchBooksResult) {
        return BookListResponse.builder()
            .booksInfo(searchBooksResult.booksResult().stream()
                .map(bookResult -> BookInfo.builder()
                    .title(bookResult.title())
                    .author(bookResult.author()).publisher(bookResult.publisher())
                    .isbn(bookResult.isbn()).tableOfContents(bookResult.tableOfContents())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }

    public static BookListResponse fromResult(NaverBookApiResult naverBookApiResult) {
        return BookListResponse.builder()
            .booksInfo(naverBookApiResult.items().stream()
                .map(item -> BookInfo.builder()
                    .title(item.title())
                    .author(item.author())
                    .publisher(item.publisher())
                    .isbn(item.isbn())
                    .tableOfContents("")
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}

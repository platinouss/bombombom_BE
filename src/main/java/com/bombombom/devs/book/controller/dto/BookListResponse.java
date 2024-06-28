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

    public static BookListResponse fromResult(SearchBooksResult searchBooksResult) {
        return BookListResponse.builder()
            .booksInfo(searchBooksResult.booksResult().stream()
                .map(bookResult -> BookInfo.builder()
                    .title(bookResult.title())
                    .author(bookResult.author())
                    .publisher(bookResult.publisher())
                    .isbn(bookResult.isbn())
                    .tableOfContents(bookResult.tableOfContents())
                    .imageUrl(bookResult.imageUrl())
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
                    .imageUrl(item.image())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}

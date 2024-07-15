package com.bombombom.devs.external.book.controller.dto;

import com.bombombom.devs.book.service.dto.SearchBooksResult;
import com.bombombom.devs.external.book.service.dto.SearchBooksResult.BookResult;
import lombok.Builder;

@Builder
public record BookInfo(
    String title,
    String author,
    String publisher,
    Long isbn,
    String tableOfContents,
    String imageUrl
) {

    public static BookInfo fromResult(BookResult bookResult) {
        return BookInfo.builder()
            .title(bookResult.title())
            .author(bookResult.author())
            .publisher(bookResult.publisher())
            .isbn(bookResult.isbn())
            .tableOfContents(bookResult.tableOfContents())
            .imageUrl(bookResult.imageUrl())
            .build();
    }

    public static BookInfo fromResult(SearchBooksResult.BookResult bookResult) {
        return BookInfo.builder()
            .title(bookResult.title())
            .author(bookResult.author())
            .publisher(bookResult.publisher())
            .isbn(bookResult.isbn())
            .tableOfContents(bookResult.tableOfContents())
            .imageUrl(bookResult.imageUrl())
            .build();
    }
}
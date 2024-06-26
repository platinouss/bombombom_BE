package com.bombombom.devs.book.service.dto;

import com.bombombom.devs.book.models.Book;
import lombok.Builder;

@Builder
public record AddBookCommand(
    String title,
    String author,
    String publisher,
    Long isbn
) {

    public Book toEntity() {
        return Book.builder()
            .title(title)
            .author(author)
            .publisher(publisher)
            .isbn(isbn)
            .tableOfContents("")
            .build();
    }
}

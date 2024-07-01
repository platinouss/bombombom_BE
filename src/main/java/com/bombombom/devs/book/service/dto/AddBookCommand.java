package com.bombombom.devs.book.service.dto;

import com.bombombom.devs.book.models.Book;
import com.bombombom.devs.book.models.BookDocument;
import lombok.Builder;

@Builder
public record AddBookCommand(
    String title,
    String author,
    String publisher,
    Long isbn,
    String imageUrl
) {

    public Book toEntity() {
        return Book.builder()
            .title(title)
            .author(author)
            .publisher(publisher)
            .isbn(isbn)
            .tableOfContents("")
            .imageUrl(imageUrl)
            .build();
    }

    public BookDocument toDocument() {
        return BookDocument.builder()
            .id(String.valueOf(isbn))
            .title(title)
            .author(author)
            .publisher(publisher)
            .tableOfContents("")
            .build();
    }
}

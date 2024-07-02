package com.bombombom.devs.book.service.dto;

import com.bombombom.devs.book.models.BookDocument;
import lombok.Builder;

@Builder
public record IndexBookCommand(
    String title,
    String author,
    String publisher,
    Long isbn,
    String imageUrl
) {

    public BookDocument toDocument() {
        return BookDocument.builder()
            .title(title)
            .author(author)
            .publisher(publisher)
            .isbn(isbn)
            .imageUrl(imageUrl)
            .tableOfContents("")
            .build();
    }
}

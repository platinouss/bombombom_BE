package com.bombombom.devs.external.book.service.dto;

import com.bombombom.devs.book.model.Book;
import lombok.Builder;

@Builder
public record IndexBookCommand(
    String title,
    String author,
    String publisher,
    Long isbn,
    String imageUrl
) {

    public static Book toBook(IndexBookCommand indexBookCommand) {
        return Book.builder()
            .title(indexBookCommand.title)
            .author(indexBookCommand.author)
            .publisher(indexBookCommand.publisher)
            .isbn(indexBookCommand.isbn)
            .imageUrl(indexBookCommand.imageUrl)
            .build();
    }
}

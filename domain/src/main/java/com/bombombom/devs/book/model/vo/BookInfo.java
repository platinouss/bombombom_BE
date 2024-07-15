package com.bombombom.devs.book.model.vo;

import com.bombombom.devs.book.model.Book;
import lombok.Builder;

@Builder
public record BookInfo(
    String title,
    String author,
    String publisher,
    Long isbn,
    String imageUrl
) {

    public static BookInfo fromBook(Book book) {
        return BookInfo.builder()
            .title(book.getTitle())
            .author(book.getAuthor())
            .publisher(book.getPublisher())
            .isbn(book.getIsbn())
            .imageUrl(book.getImageUrl())
            .build();
    }
}

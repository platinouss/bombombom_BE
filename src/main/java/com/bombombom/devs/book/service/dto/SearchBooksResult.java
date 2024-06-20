package com.bombombom.devs.book.service.dto;

import com.bombombom.devs.book.models.Book;
import java.util.List;
import lombok.Builder;

@Builder
public record SearchBooksResult(
    List<BookResult> booksResult
) {

    @Builder
    public record BookResult(
        Long id,
        String title,
        String author,
        String publisher,
        Long isbn,
        String tableOfContents
    ) {

    }

    public static BookResult fromEntity(Book book) {
        return BookResult.builder()
            .id(book.getId())
            .title(book.getTitle())
            .author(book.getAuthor())
            .publisher(book.getPublisher())
            .isbn(book.getIsbn())
            .tableOfContents(book.getTableOfContents())
            .build();
    }
}

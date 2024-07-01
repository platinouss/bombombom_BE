package com.bombombom.devs.book.service.dto;

import com.bombombom.devs.book.models.BookDocument;
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
        String tableOfContents,
        String imageUrl
    ) {

    }

    public static BookResult fromDocument(BookDocument bookDocument) {
        return BookResult.builder()
            .title(bookDocument.getTitle())
            .author(bookDocument.getAuthor())
            .publisher(bookDocument.getPublisher())
            .isbn(Long.parseLong(bookDocument.getId()))
            .tableOfContents(bookDocument.getTableOfContents())
            .imageUrl(bookDocument.getImageUrl())
            .build();
    }
}

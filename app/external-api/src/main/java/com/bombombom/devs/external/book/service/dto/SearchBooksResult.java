package com.bombombom.devs.external.book.service.dto;

import com.bombombom.devs.book.model.Book;
import com.bombombom.devs.book.model.BookDocument;
import com.bombombom.devs.dto.NaverBookApiResult;
import java.util.List;
import java.util.Objects;
import lombok.Builder;

@Builder
public record SearchBooksResult(
    List<BookResult> bookResults
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

    public static BookResult fromBookDocument(BookDocument bookDocument) {
        return BookResult.builder()
            .id(bookDocument.getBookId())
            .title(bookDocument.getTitle())
            .author(bookDocument.getAuthor())
            .publisher(bookDocument.getPublisher())
            .isbn(bookDocument.getIsbn())
            .tableOfContents(bookDocument.getTableOfContents())
            .imageUrl(bookDocument.getImageUrl())
            .build();
    }

    public static BookResult fromBook(Book book) {
        return BookResult.builder()
            .id(book.getId())
            .title(book.getTitle())
            .author(book.getAuthor())
            .publisher(book.getPublisher())
            .isbn(book.getIsbn())
            .tableOfContents(book.getTableOfContents())
            .imageUrl(book.getImageUrl())
            .build();
    }

    public static SearchBooksResult fromNaverBookApiResult(NaverBookApiResult naverBookApiResult) {
        List<BookResult> bookResults = naverBookApiResult.items().stream()
            .filter(item -> Objects.nonNull(item.isbn()))
            .map(item -> BookResult.builder()
                .title(item.title())
                .author(item.author())
                .publisher(item.publisher())
                .isbn(item.isbn())
                .imageUrl(item.image())
                .build()
            ).toList();
        return SearchBooksResult.builder().bookResults(bookResults).build();
    }

    public List<IndexBookCommand> toServiceDto() {
        return bookResults().stream()
            .map(book -> IndexBookCommand.builder()
                .title(book.title)
                .author(book.author)
                .publisher(book.publisher)
                .isbn(book.isbn)
                .imageUrl(book.imageUrl)
                .build())
            .toList();
    }
}

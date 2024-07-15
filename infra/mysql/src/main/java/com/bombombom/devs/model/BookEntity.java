package com.bombombom.devs.model;

import com.bombombom.devs.book.model.Book;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Entity
@Table(name = "book")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false, unique = true)
    private Long isbn;

    @Column(name = "table_of_contents")
    private String tableOfContents;

    @Column(name = "image_url")
    private String imageUrl;

    public static BookEntity fromBook(Book book) {
        return BookEntity.builder()
            .title(book.getTitle())
            .author(book.getAuthor())
            .publisher(book.getPublisher())
            .isbn(book.getIsbn())
            .tableOfContents(book.getTableOfContents())
            .imageUrl(book.getImageUrl())
            .build();
    }

    public static Book toBook(BookEntity bookEntity) {
        return Book.builder()
            .bookId(bookEntity.id)
            .title(bookEntity.title)
            .author(bookEntity.author)
            .publisher(bookEntity.publisher)
            .isbn(bookEntity.isbn)
            .tableOfContents(bookEntity.tableOfContents)
            .imageUrl(bookEntity.imageUrl)
            .build();
    }
}

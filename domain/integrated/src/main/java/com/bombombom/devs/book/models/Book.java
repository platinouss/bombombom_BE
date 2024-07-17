package com.bombombom.devs.book.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

// TODO: study 도메인 모듈 전환 후 제거 필요
@SuperBuilder
@Entity(name = "test")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

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

    public static Book fromDocument(BookDocument bookDocument) {
        return Book.builder()
            .title(bookDocument.getTitle())
            .author(bookDocument.getAuthor())
            .publisher(bookDocument.getPublisher())
            .isbn(bookDocument.getIsbn())
            .tableOfContents(bookDocument.getTableOfContents())
            .imageUrl(bookDocument.getImageUrl())
            .build();
    }
}

package com.bombombom.devs.book.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class Book {

    @Setter
    private Long bookId;
    private String title;
    private String author;
    private String publisher;
    private Long isbn;
    private String tableOfContents;
    private String imageUrl;
}

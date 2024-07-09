package com.bombombom.devs.model;

import com.bombombom.devs.book.model.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@Document(indexName = "book")
public class BookDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long bookId;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Keyword)
    private String author;

    @Field(type = FieldType.Keyword, index = false)
    private String publisher;

    @Field(type = FieldType.Long)
    private Long isbn;

    @Field(type = FieldType.Text, index = false)
    private String imageUrl;

    @Field(type = FieldType.Text)
    private String tableOfContents;

    public static BookDocument fromBook(Book book) {
        return BookDocument.builder()
            .id(String.valueOf(book.getBookId()))
            .title(book.getTitle())
            .author(book.getAuthor())
            .publisher(book.getPublisher())
            .isbn(book.getIsbn())
            .imageUrl(book.getImageUrl())
            .tableOfContents(book.getTableOfContents())
            .build();
    }

    public static Book toBook(BookDocument bookDocument) {
        return Book.builder()
            .title(bookDocument.title)
            .author(bookDocument.author)
            .publisher(bookDocument.publisher)
            .isbn(bookDocument.isbn)
            .imageUrl(bookDocument.imageUrl)
            .tableOfContents(bookDocument.tableOfContents)
            .build();
    }
}

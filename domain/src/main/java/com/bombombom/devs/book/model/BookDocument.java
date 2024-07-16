package com.bombombom.devs.book.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@SuperBuilder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Document(indexName = "book")
public class BookDocument {

    @Id
    private String id;

    @Setter
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
}

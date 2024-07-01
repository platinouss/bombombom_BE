package com.bombombom.devs.book.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Document(indexName = "book-test")
public class BookDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Keyword)
    private String author;

    @Field(type = FieldType.Keyword, index = false)
    private String publisher;

    @Field(type = FieldType.Text, index = false)
    private String image;

    @Field(type = FieldType.Text, index = false)
    private String tableOfContents;
}

package com.bombombom.devs.book.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

// TODO: study 도메인 모듈 전환 후 제거 필요
@Getter
@Builder
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

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}

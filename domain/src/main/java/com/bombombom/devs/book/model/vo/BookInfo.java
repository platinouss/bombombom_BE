package com.bombombom.devs.book.model.vo;

import lombok.Builder;

@Builder
public record BookInfo(
    String title,
    String author,
    String publisher,
    Long isbn,
    String imageUrl
) {

}

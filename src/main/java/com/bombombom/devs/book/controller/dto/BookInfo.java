package com.bombombom.devs.book.controller.dto;

import lombok.Builder;

@Builder
public record BookInfo(
    String title,
    String author,
    String publisher,
    Long isbn,
    String tableOfContents
) {

}
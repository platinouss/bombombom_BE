package com.bombombom.devs.book.dto;

import lombok.Builder;

@Builder
public record IndexBookCommand(
    String title,
    String author,
    String publisher,
    Long isbn,
    String imageUrl
) {

}

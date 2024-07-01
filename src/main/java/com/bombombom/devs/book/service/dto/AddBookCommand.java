package com.bombombom.devs.book.service.dto;

import lombok.Builder;

@Builder
public record AddBookCommand(
    String isbn
) {

}

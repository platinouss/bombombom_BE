package com.bombombom.devs.external.book.service.dto;

import lombok.Builder;

@Builder
public record AddBookCommand(
    Long isbn
) {

}

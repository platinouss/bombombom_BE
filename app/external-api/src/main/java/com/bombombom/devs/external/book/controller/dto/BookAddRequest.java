package com.bombombom.devs.external.book.controller.dto;

import com.bombombom.devs.book.service.dto.AddBookCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BookAddRequest(
    @NotNull(message = "공백일 수 없습니다.") Long isbn
) {

    public AddBookCommand toServiceDto() {
        return AddBookCommand.builder()
            .isbn(isbn)
            .build();
    }
}

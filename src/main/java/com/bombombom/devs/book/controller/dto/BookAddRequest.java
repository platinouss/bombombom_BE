package com.bombombom.devs.book.controller.dto;

import com.bombombom.devs.book.service.dto.AddBookCommand;
import jakarta.validation.constraints.NotBlank;

public record BookAddRequest(
    @NotBlank(message = "공백일 수 없습니다.") Long isbn
) {

    public AddBookCommand toServiceDto() {
        return AddBookCommand.builder()
            .isbn(String.valueOf(isbn))
            .build();
    }
}

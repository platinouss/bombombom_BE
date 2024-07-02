package com.bombombom.devs.book.controller.dto;

import com.bombombom.devs.book.service.dto.AddBookCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record BookAddRequest(
    @NotBlank(message = "공백일 수 없습니다.") String isbn
) {

    public AddBookCommand toServiceDto() {
        return AddBookCommand.builder()
            .isbn(Long.parseLong(isbn))
            .build();
    }
}

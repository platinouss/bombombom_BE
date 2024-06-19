package com.bombombom.devs.book.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record BookAddRequest(@NotBlank(message = "공백일 수 없습니다.") String keyword) {

}

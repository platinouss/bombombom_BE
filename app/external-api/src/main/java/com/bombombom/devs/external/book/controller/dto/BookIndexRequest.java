package com.bombombom.devs.external.book.controller.dto;

import com.bombombom.devs.book.service.dto.NaverBookApiQuery;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record BookIndexRequest(
    @NotBlank(message = "공백일 수 없습니다.") String keyword
) {

    public NaverBookApiQuery toServiceDto() {
        return new NaverBookApiQuery(keyword);
    }
}

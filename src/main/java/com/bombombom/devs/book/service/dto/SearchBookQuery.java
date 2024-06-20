package com.bombombom.devs.book.service.dto;

import com.bombombom.devs.book.enums.SearchOption;
import lombok.Builder;

@Builder
public record SearchBookQuery(
    String keyword,
    SearchOption searchOption
) {

}

package com.bombombom.devs.external.book.service.dto;

import com.bombombom.devs.external.book.enums.SearchOption;
import lombok.Builder;

@Builder
public record SearchBookQuery(
    String keyword,
    SearchOption searchOption
) {

}

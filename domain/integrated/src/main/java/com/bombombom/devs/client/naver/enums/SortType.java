package com.bombombom.devs.client.naver.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SortType {
    SIMILARITY("sim"),
    RECENTLY_PUBLISHED("date");

    public final String value;

    SortType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

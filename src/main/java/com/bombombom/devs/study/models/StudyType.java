package com.bombombom.devs.study.models;

import lombok.ToString;

@ToString
public enum StudyType {

    ALGORITHM(Values.ALGORITHM),
    BOOK(Values.BOOK);

    StudyType(String val) {
        if (!this.name().equals(val))
            throw new IllegalArgumentException("Incorrect use of StudyType");
    }

    public static class Values {
        public static final String ALGORITHM = "ALGORITHM";
        public static final String BOOK = "BOOK";
    }
}

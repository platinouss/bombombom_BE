package com.bombombom.devs.study.enums;

import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.ServerInternalException;
import lombok.ToString;

@ToString
public enum StudyType {

    ALGORITHM(Values.ALGORITHM),
    BOOK(Values.BOOK);

    StudyType(String val) {
        if (!this.name().equals(val)) {
            throw new ServerInternalException(ErrorCode.INCORRECT_STUDY_TYPE);
        }
    }

    public static class Values {

        public static final String ALGORITHM = "ALGORITHM";
        public static final String BOOK = "BOOK";
    }
}

package com.bombombom.devs.book.exception;

import com.bombombom.devs.study.exception.NotFoundException;

public class BookNotFoundException extends NotFoundException {

    public BookNotFoundException() {
        super("해당 서적이 존재하지 않습니다.");
    }
}

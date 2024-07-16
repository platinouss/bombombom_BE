package com.bombombom.devs.external.book.exception;


import com.bombombom.devs.external.study.exception.NotFoundException;

public class BookNotFoundException extends NotFoundException {

    public BookNotFoundException() {
        super("해당 서적이 존재하지 않습니다.");
    }
}

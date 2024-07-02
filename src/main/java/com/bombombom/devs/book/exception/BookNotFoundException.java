package com.bombombom.devs.book.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException() {
        super("해당 서적이 존재하지 않습니다.");
    }
}

package com.bombombom.devs.user.exception;

public class ExistUsernameException extends RuntimeException {

    public ExistUsernameException() {
        super("존재하는 username 입니다.");
    }
}

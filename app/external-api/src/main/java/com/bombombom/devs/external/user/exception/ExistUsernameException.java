package com.bombombom.devs.external.user.exception;

public class ExistUsernameException extends RuntimeException {

    public ExistUsernameException() {
        super("존재하는 username 입니다.");
    }
}

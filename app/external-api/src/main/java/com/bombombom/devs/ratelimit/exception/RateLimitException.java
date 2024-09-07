package com.bombombom.devs.ratelimit.exception;

public class RateLimitException extends RuntimeException {

    public RateLimitException() {
        super("Rate limit exceeded");
    }
}

package com.bombombom.devs.core.exception;

public class RateLimitException extends RuntimeException {

    public RateLimitException(String bucketName) {
        super(bucketName + " bucket rate limit exceeded");
    }
}

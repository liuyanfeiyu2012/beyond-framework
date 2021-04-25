package org.lyfy.beyond.ratelimiter.exception;

public class RateLimitException extends RuntimeException {

    public RateLimitException(String msg) {
        super(msg);
    }

}

package com.syos.exception;

public class SyosException extends RuntimeException {

    public SyosException(String message) {
        super(message);
    }

    public SyosException(String message, Throwable cause) {
        super(message, cause);
    }
}
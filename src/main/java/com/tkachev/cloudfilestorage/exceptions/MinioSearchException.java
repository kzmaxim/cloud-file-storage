package com.tkachev.cloudfilestorage.exceptions;

public class MinioSearchException extends RuntimeException {
    public MinioSearchException(String message) {
        super(message);
    }

    public MinioSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}

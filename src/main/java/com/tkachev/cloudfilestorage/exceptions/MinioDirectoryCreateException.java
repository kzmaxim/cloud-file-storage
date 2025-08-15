package com.tkachev.cloudfilestorage.exceptions;

public class MinioDirectoryCreateException extends RuntimeException {
    public MinioDirectoryCreateException(String message) {
        super(message);
    }

    public MinioDirectoryCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}

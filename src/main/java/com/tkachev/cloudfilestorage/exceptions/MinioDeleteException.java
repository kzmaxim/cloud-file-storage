package com.tkachev.cloudfilestorage.exceptions;

public class MinioDeleteException extends RuntimeException {
    public MinioDeleteException(String message) {
        super(message);
    }

    public MinioDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}

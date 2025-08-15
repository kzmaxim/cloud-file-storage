package com.tkachev.cloudfilestorage.exceptions;

public class MinioFileUploadException extends RuntimeException {

    public MinioFileUploadException(String message) {
        super(message);
    }

    public MinioFileUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinioFileUploadException(Throwable cause) {
        super(cause);
    }
}

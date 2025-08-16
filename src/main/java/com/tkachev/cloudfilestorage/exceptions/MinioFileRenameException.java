package com.tkachev.cloudfilestorage.exceptions;

public class MinioFileRenameException extends RuntimeException {
    public MinioFileRenameException(String message) {
        super(message);
    }

    public MinioFileRenameException(String message, Throwable cause) {
        super(message, cause);
    }
}

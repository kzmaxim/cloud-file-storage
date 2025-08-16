package com.tkachev.cloudfilestorage.exceptions;

public class MinioFolderReadException extends RuntimeException {
    public MinioFolderReadException(String message) {
        super(message);
    }

    public MinioFolderReadException(String message, Throwable cause) {
        super(message, cause);
    }
}

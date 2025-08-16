package com.tkachev.cloudfilestorage.exceptions;

public class MinioDirectoryRenameException extends RuntimeException {
    public MinioDirectoryRenameException(String message) {
        super(message);
    }

    public MinioDirectoryRenameException(String message, Throwable cause) {
        super(message, cause);
    }
}

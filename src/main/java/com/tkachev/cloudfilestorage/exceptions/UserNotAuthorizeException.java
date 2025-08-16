package com.tkachev.cloudfilestorage.exceptions;

public class UserNotAuthorizeException extends RuntimeException {
    public UserNotAuthorizeException(String message) {
        super(message);
    }
    public UserNotAuthorizeException(String message, Throwable cause) {
        super(message, cause);
    }
}

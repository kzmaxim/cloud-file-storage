package com.tkachev.cloudfilestorage.exceptions;

public class PathNotFoundException extends RuntimeException {
    public PathNotFoundException(String message) {
        super(message);
    }
    public PathNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

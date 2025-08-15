package com.tkachev.cloudfilestorage.exceptions;

import com.tkachev.cloudfilestorage.dto.ErrorDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder errors = new StringBuilder();
        ex.getAllErrors().forEach(error -> errors.append(error.getDefaultMessage()).append("; "));
        return ResponseEntity.status(401).body(new ErrorDTO(errors.toString()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(404).body(new ErrorDTO(ex.getMessage()));
    }

    @ExceptionHandler(PathNotFoundException.class)
    public ResponseEntity<ErrorDTO> handlePathNotFoundException(PathNotFoundException ex) {
        return ResponseEntity.status(400).body(new ErrorDTO(ex.getMessage()));
    }

    @ExceptionHandler(UserNotAuthorizeException.class)
    public ResponseEntity<ErrorDTO> handleUserNotAuthorizeException(UserNotAuthorizeException ex) {
        return ResponseEntity.status(401).body(new ErrorDTO(ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(404).body(new ErrorDTO(ex.getMessage()));
    }

    @ExceptionHandler(MinioFileUploadException.class)
    public ResponseEntity<ErrorDTO> handleMinioFileUploadException(MinioFileUploadException ex) {
        return ResponseEntity.status(500).body(new ErrorDTO("File upload failed: " + ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> handleException(Exception ex) {
        return ResponseEntity.status(500).body(new ErrorDTO("An unexpected error occurred: " + ex.getMessage()));
    }
}


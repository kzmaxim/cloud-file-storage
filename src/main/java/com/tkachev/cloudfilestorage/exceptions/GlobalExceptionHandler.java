package com.tkachev.cloudfilestorage.exceptions;

import com.tkachev.cloudfilestorage.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO("File upload failed: " + ex.getMessage()));
    }

    @ExceptionHandler(MinioDirectoryCreateException.class)
    public ResponseEntity<ErrorDTO> handleMinioDirectoryCreateException(MinioDirectoryCreateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO("Directory creation failed: " + ex.getMessage()));
    }

    @ExceptionHandler(MinioFileRenameException.class)
    public ResponseEntity<ErrorDTO> handleMinioFileRenameException(MinioFileRenameException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO("File rename failed: " + ex.getMessage()));
    }

    @ExceptionHandler(MinioDirectoryRenameException.class)
    public ResponseEntity<ErrorDTO> handleMinioDirectoryRenameException(MinioDirectoryRenameException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO("Directory rename failed: " + ex.getMessage()));
    }

    @ExceptionHandler(MinioFolderReadException.class)
    public ResponseEntity<ErrorDTO> handleMinioFolderReadException(MinioFolderReadException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDTO("Failed to read folder: " + ex.getMessage()));
    }

    @ExceptionHandler(MinioDeleteException.class)
    public ResponseEntity<ErrorDTO> handleMinioDeleteException(MinioDeleteException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDTO("Resource deletion failed: " + ex.getMessage()));
    }

    @ExceptionHandler(MinioSearchException.class)
    public ResponseEntity<ErrorDTO> handleMinioSearchException(MinioSearchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO("Search operation failed: " + ex.getMessage()));
    }


    @ExceptionHandler
    public ResponseEntity<ErrorDTO> handleException(Exception ex) {
        return ResponseEntity.status(500).body(new ErrorDTO("An unexpected error occurred: " + ex.getMessage()));
    }
}


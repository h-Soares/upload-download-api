package com.soaresdev.uploaddownloadapi.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;

@RestControllerAdvice
public class ControllersExceptionHandler {
    @ExceptionHandler(FileInternalErrorException.class)
    public ResponseEntity<StandardError> fileInternalError(FileInternalErrorException e, HttpServletRequest request) {
        return ResponseEntity.internalServerError().body(getStandardError(HttpStatus.INTERNAL_SERVER_ERROR, e, request));
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<StandardError> fileNotFound(FileNotFoundException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getStandardError(HttpStatus.NOT_FOUND, e, request));
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<StandardError> fileUpload(FileUploadException e, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(getStandardError(HttpStatus.BAD_REQUEST, e, request));
    }

    private StandardError getStandardError(HttpStatus hs, Exception e, HttpServletRequest request) {
        StandardError standardError = new StandardError();
        standardError.setTimestamp(Instant.now());
        standardError.setStatus(hs.value());
        standardError.setError(e.getClass().getSimpleName());
        standardError.setMessage(e.getMessage());
        standardError.setPath(request.getRequestURI());
        return standardError;
    }
}
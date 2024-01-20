package com.soaresdev.uploaddownloadapi.exceptions;

import java.io.Serial;

public class FileUploadException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public FileUploadException(String message) {
        super(message);
    }
}
package com.soaresdev.uploaddownloadapi.exceptions;

import java.io.Serial;

public class FileInternalErrorException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public FileInternalErrorException(String message) {
        super(message);
    }
}
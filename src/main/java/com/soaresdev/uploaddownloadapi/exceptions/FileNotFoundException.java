package com.soaresdev.uploaddownloadapi.exceptions;

import java.io.Serial;

public class FileNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public FileNotFoundException(String message) {
        super(message);
    }
}
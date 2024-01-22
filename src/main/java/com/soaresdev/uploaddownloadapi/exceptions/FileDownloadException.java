package com.soaresdev.uploaddownloadapi.exceptions;

import java.io.Serial;

public class FileDownloadException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public FileDownloadException(String message) {
        super(message);
    }
}
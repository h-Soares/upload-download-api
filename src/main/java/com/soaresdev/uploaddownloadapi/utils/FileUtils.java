package com.soaresdev.uploaddownloadapi.utils;

public class FileUtils {
    private static final String REGEX_FILE_NAME = "^[a-zA-Z0-9](?:[a-zA-Z0-9._-]*[a-zA-Z0-9])?\\.[a-zA-Z0-9_-]+$";

    private FileUtils() {
    }

    public static boolean isValidFileName(String fileName) {
        return fileName.matches(REGEX_FILE_NAME);
    }
}
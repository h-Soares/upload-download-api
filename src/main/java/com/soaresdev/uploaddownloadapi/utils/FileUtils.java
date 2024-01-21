package com.soaresdev.uploaddownloadapi.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class FileUtils {
    private static final String REGEX_FILE_NAME = "^[a-zA-Z0-9](?:[a-zA-Z0-9. _-]*[a-zA-Z0-9])?\\.[a-zA-Z0-9_-]+$";

    private FileUtils() {
    }

    public static boolean isValidFileName(String fileName) {
        return fileName.matches(REGEX_FILE_NAME);
    }

    public static String getFileDownloadUri(String fileName) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/download")
                .path("/{fileName}")
                .buildAndExpand(fileName)
                .toUriString().replace("/upload", "");
    }

    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }
}
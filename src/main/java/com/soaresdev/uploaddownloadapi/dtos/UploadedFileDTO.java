package com.soaresdev.uploaddownloadapi.dtos;

import java.io.Serial;
import java.io.Serializable;

public class UploadedFileDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private String fileSize;

    public UploadedFileDTO() {
    }

    public UploadedFileDTO(String fileName, String fileDownloadUri, String fileType, String fileSize) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
}
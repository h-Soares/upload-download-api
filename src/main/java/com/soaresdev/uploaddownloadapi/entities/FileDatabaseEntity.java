package com.soaresdev.uploaddownloadapi.entities;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_file")
public class FileDatabaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String fileName;
    @Column(nullable = false)
    private String fileDownloadUri;
    @Column(nullable = false)
    private String fileType;
    @Column(nullable = false)
    private long fileByteSize;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] binaryData;

    public FileDatabaseEntity() {
    }

    public FileDatabaseEntity(UUID id, String fileName, String fileDownloadUri, String fileType, long fileByteSize, byte[] binaryData) {
        this.id = id;
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.fileByteSize = fileByteSize;
        this.binaryData = binaryData;
    }

    public FileDatabaseEntity(String fileName, String fileDownloadUri, String fileType, long fileByteSize, byte[] binaryData) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.fileByteSize = fileByteSize;
        this.binaryData = binaryData;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public long getFileByteSize() {
        return fileByteSize;
    }

    public void setFileByteSize(long fileSize) {
        this.fileByteSize = fileSize;
    }

    public byte[] getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileDatabaseEntity that = (FileDatabaseEntity) o;
        return Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName);
    }
}
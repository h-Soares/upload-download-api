package com.soaresdev.uploaddownloadapi.services;

import com.soaresdev.uploaddownloadapi.configs.FileSystemConfig;
import com.soaresdev.uploaddownloadapi.dtos.UploadedFileDTO;
import com.soaresdev.uploaddownloadapi.exceptions.FileDownloadException;
import com.soaresdev.uploaddownloadapi.exceptions.FileInternalErrorException;
import com.soaresdev.uploaddownloadapi.exceptions.FileNotFoundException;
import com.soaresdev.uploaddownloadapi.exceptions.FileUploadException;
import com.soaresdev.uploaddownloadapi.utils.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileSystemService {
    private final Path fileUploadLocation;

    public FileSystemService(FileSystemConfig fileSystemConfig) {
        fileUploadLocation = Paths.get(fileSystemConfig.getUploadDirectory()).
                toAbsolutePath().normalize();

        try {
            Files.createDirectories(fileUploadLocation);
        }catch(Exception e) {
            throw new FileInternalErrorException("Fatal error while creating file upload directory");
        }
    }

    public UploadedFileDTO uploadFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        if(fileName == null || !FileUtils.isValidFileName(fileName))
            throw new FileUploadException("Invalid file name: " + fileName);

        try {
            Path targetLocation = fileUploadLocation.resolve(fileName);
            if(Files.exists(targetLocation))
                throw new FileUploadException("File already exists: " + fileName);

            String downloadUri = FileUtils.getFileDownloadUri(fileName);

            file.transferTo(targetLocation);
            return new UploadedFileDTO(fileName, downloadUri, file.getContentType(), FileUtils.humanReadableByteCountSI(file.getSize()));
        }catch(IOException e) {
            throw new FileUploadException("Fatal error. Could not upload file: " + fileName);
        }
    }

    public List<UploadedFileDTO> uploadFiles(List<MultipartFile> files) {
        return files.stream().map(this::uploadFile).toList();
    }

    public Resource downloadFile(String fileName) {
        if(!FileUtils.isValidFileName(fileName))
            throw new FileDownloadException("Invalid file name: " + fileName);

        try {
            Path filePath = fileUploadLocation.resolve(fileName).normalize();
            Resource downloadedFile = new UrlResource(filePath.toUri());

            if(!downloadedFile.exists())
                throw new FileNotFoundException("File not found: " + fileName);
            return downloadedFile;
        }catch(MalformedURLException e) {
            throw new FileDownloadException("Fatal error. Could not download file: " + fileName);
        }
    }
}
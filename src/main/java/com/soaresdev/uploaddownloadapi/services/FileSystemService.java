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
import java.util.ArrayList;
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
        verifyFileName(fileName);

        try {
            Path targetLocation = fileUploadLocation.resolve(fileName);
            verifyIfFileExists(targetLocation);

            String downloadUri = FileUtils.getFileDownloadUri(fileName);

            file.transferTo(targetLocation);
            return new UploadedFileDTO(fileName, downloadUri, file.getContentType(), FileUtils.humanReadableByteCountSI(file.getSize()));
        }catch(IOException e) {
            throw new FileUploadException("Fatal error. Could not upload file: " + fileName);
        }
    }

    public List<UploadedFileDTO> uploadFiles(List<MultipartFile> files) {
        //this "for" is used as a @Transactional for upload files in system
        for(MultipartFile file : files) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            verifyFileName(fileName);

            Path targetLocation = fileUploadLocation.resolve(fileName);
            verifyIfFileExists(targetLocation);
        }
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

    public List<UploadedFileDTO> listAllFiles() {
        try {
            List<Path> pathFilesList = Files.list(fileUploadLocation).toList();

            List<UploadedFileDTO> fileList = new ArrayList<>();
            for(Path pathFile : pathFilesList) {
                fileList.add(new UploadedFileDTO(pathFile.getFileName().toString(),
                        FileUtils.getFileDownloadUri(pathFile.getFileName().toString()),
                        Files.probeContentType(pathFile),
                        FileUtils.humanReadableByteCountSI(Files.size(pathFile))));
            }
            return fileList;
        }catch(Exception e) {
            throw new FileInternalErrorException("Fatal error. Could not list all files");
        }
    }

    private static void verifyFileName(String fileName) {
        if(fileName == null || !FileUtils.isValidFileName(fileName))
            throw new FileUploadException("Invalid file name: " + fileName);
    }

    private static void verifyIfFileExists(Path targetLocation) {
        if(Files.exists(targetLocation))
            throw new FileUploadException("File already exists: " + targetLocation.getFileName());
    }
}
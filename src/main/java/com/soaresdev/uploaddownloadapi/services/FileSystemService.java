package com.soaresdev.uploaddownloadapi.services;

import com.soaresdev.uploaddownloadapi.configs.FileSystemConfig;
import com.soaresdev.uploaddownloadapi.dtos.UploadedFileDTO;
import com.soaresdev.uploaddownloadapi.exceptions.FileInternalErrorException;
import com.soaresdev.uploaddownloadapi.exceptions.FileUploadException;
import com.soaresdev.uploaddownloadapi.utils.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
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
            throw new FileInternalErrorException("Error while creating file upload directory");
        }
    }

    public UploadedFileDTO uploadFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        if(fileName == null || !FileUtils.isValidFileName(fileName))
            throw new FileUploadException("Invalid file name: " + fileName);

        try {
            Path targetLocation = fileUploadLocation.resolve(fileName);
            if(Files.exists(targetLocation))
                throw new FileUploadException("File already exists");

            String downloadUri = FileUtils.getFileDownloadUri(fileName);

            file.transferTo(targetLocation);
            return new UploadedFileDTO(fileName, downloadUri, file.getContentType(), FileUtils.humanReadableByteCountSI(file.getSize()));
        }catch(Exception e) {
            throw new FileUploadException("Could not upload file " + fileName + " because " + e.getMessage());
        }
    }

    public List<UploadedFileDTO> uploadFiles(List<MultipartFile> files) {
        return files.stream().map(this::uploadFile).toList();
    }
}
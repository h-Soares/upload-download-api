package com.soaresdev.uploaddownloadapi.services;

import com.soaresdev.uploaddownloadapi.dtos.UploadedFileDTO;
import com.soaresdev.uploaddownloadapi.entities.FileDatabaseEntity;
import com.soaresdev.uploaddownloadapi.exceptions.FileDownloadException;
import com.soaresdev.uploaddownloadapi.exceptions.FileNotFoundException;
import com.soaresdev.uploaddownloadapi.exceptions.FileUploadException;
import com.soaresdev.uploaddownloadapi.repositories.FileDatabaseRepository;
import com.soaresdev.uploaddownloadapi.utils.FileUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
public class FileDatabaseService {
    private final FileDatabaseRepository fileDatabaseRepository;

    public FileDatabaseService(FileDatabaseRepository fileDatabaseRepository) {
        this.fileDatabaseRepository = fileDatabaseRepository;
    }

    @Transactional
    public UploadedFileDTO uploadFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        if(fileName == null || !FileUtils.isValidFileName(fileName))
            throw new FileUploadException("Invalid file name: " + fileName);

        if(fileDatabaseRepository.existsByFileName(fileName))
            throw new FileUploadException("File already exists: " + fileName);

        try {
            FileDatabaseEntity toUploadFile = new FileDatabaseEntity(fileName,
                    file.getContentType(),
                    file.getSize(),
                    file.getBytes());

            fileDatabaseRepository.save(toUploadFile);
            return new UploadedFileDTO(fileName,
                    FileUtils.getFileDownloadUri(fileName),
                    file.getContentType(),
                    FileUtils.humanReadableByteCountSI(file.getSize()));
        }catch(IOException e) {
            throw new FileUploadException("Fatal error. Could not upload file: " + fileName);
        }
    }

    @Transactional
    public List<UploadedFileDTO> uploadFiles(List<MultipartFile> files) {
        return files.stream().map(this::uploadFile).toList();
    }

    @Transactional
    public FileDatabaseEntity downloadFile(String fileName) {
        if(!FileUtils.isValidFileName(fileName))
            throw new FileDownloadException("Invalid file name: " + fileName);

        FileDatabaseEntity downloadedFile = fileDatabaseRepository.findByFileName(fileName).
                orElseThrow(() -> new FileNotFoundException("File not found: " + fileName));

        return downloadedFile;
    }

    public List<UploadedFileDTO> listAllFiles() {
        return fileDatabaseRepository.findAll().stream().map(file ->
                new UploadedFileDTO(file.getFileName(),
                        FileUtils.getFileDownloadUri(file.getFileName()),
                        file.getFileType(),
                        FileUtils.humanReadableByteCountSI(file.getFileByteSize()))).
                toList();
    }
}
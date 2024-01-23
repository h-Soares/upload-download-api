package com.soaresdev.uploaddownloadapi.services;

import com.soaresdev.uploaddownloadapi.dtos.UploadedFileDTO;
import com.soaresdev.uploaddownloadapi.entities.FileDatabaseEntity;
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

        String downloadUri = FileUtils.getFileDownloadUri(fileName);
        try {
            FileDatabaseEntity toUploadFile = new FileDatabaseEntity(fileName,
                    downloadUri,
                    file.getContentType(),
                    file.getSize(),
                    file.getBytes());

            fileDatabaseRepository.save(toUploadFile);
            return new UploadedFileDTO(fileName,
                    downloadUri,
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

}
package com.soaresdev.uploaddownloadapi.controllers.v1;

import com.soaresdev.uploaddownloadapi.dtos.UploadedFileDTO;
import com.soaresdev.uploaddownloadapi.entities.FileDatabaseEntity;
import com.soaresdev.uploaddownloadapi.exceptions.StandardError;
import com.soaresdev.uploaddownloadapi.services.FileDatabaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/v1/in-database/files")
@Tag(name = "Database system", description = "Allows manipulate files on database")
public class FileDatabaseController {
    private final FileDatabaseService fileDatabaseService;

    public FileDatabaseController(FileDatabaseService fileDatabaseService) {
        this.fileDatabaseService = fileDatabaseService;
    }

    @Operation(description = "Upload file into database", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UploadedFileDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error in file upload", content = @Content(schema = @Schema(implementation = StandardError.class))),
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UploadedFileDTO> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fileDatabaseService.uploadFile(file));
    }

    @Operation(description = "Upload files into database", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UploadedFileDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error in files upload", content = @Content(schema = @Schema(implementation = StandardError.class))),
    })
    @PostMapping(value = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UploadedFileDTO>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.ok(fileDatabaseService.uploadFiles(files));
    }

    @Operation(description = "Download file from file database", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Error in file download", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "File not found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
    })
    @GetMapping(value = "/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        FileDatabaseEntity downloadedFile = fileDatabaseService.downloadFile(fileName);

        return ResponseEntity.ok().
                contentType(MediaType.parseMediaType(downloadedFile.getFileType())).
                header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + downloadedFile.getFileName()).
                body(new ByteArrayResource(downloadedFile.getBinaryData()));
    }
}
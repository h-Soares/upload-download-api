package com.soaresdev.uploaddownloadapi.controllers.v1;

import com.soaresdev.uploaddownloadapi.dtos.UploadedFileDTO;
import com.soaresdev.uploaddownloadapi.exceptions.FileDownloadException;
import com.soaresdev.uploaddownloadapi.exceptions.StandardError;
import com.soaresdev.uploaddownloadapi.services.FileSystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/v1/in-system/files")
@Tag(name = "File system", description = "Allows manipulate files on the system")
public class FileSystemController {
    private final FileSystemService fileSystemService;

    public FileSystemController(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
    }

    @Operation(description = "Upload file into file system", method = "POST")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UploadedFileDTO.class))),
        @ApiResponse(responseCode = "400", description = "Error in file upload", content = @Content(schema = @Schema(implementation = StandardError.class))),
        @ApiResponse(responseCode = "500", description = "Internal file system error", content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UploadedFileDTO> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fileSystemService.uploadFile(file));
    }

    @Operation(description = "Upload files into file system", method = "POST")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UploadedFileDTO.class))),
        @ApiResponse(responseCode = "400", description = "Error in files upload", content = @Content(schema = @Schema(implementation = StandardError.class))),
        @ApiResponse(responseCode = "500", description = "Internal file system error", content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @PostMapping(value = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UploadedFileDTO>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.ok(fileSystemService.uploadFiles(files));
    }


    @Operation(description = "Download file from file system", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Error in file download", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "File not found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal file system error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)))
    })
    @GetMapping(value = "/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        Resource downloadedFile = fileSystemService.downloadFile(fileName);

        try {
            String contentType = request.getServletContext().getMimeType(downloadedFile.getFile().getAbsolutePath());

            if(contentType == null)
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).
                    header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=" + downloadedFile.getFilename()).
                    body(downloadedFile);
        }catch(Exception e) {
            throw new FileDownloadException("Could not determine file type of file: " + fileName);
        }
    }
}
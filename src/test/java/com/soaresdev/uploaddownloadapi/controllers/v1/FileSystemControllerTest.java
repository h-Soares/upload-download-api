package com.soaresdev.uploaddownloadapi.controllers.v1;

import com.soaresdev.uploaddownloadapi.configs.FileSystemConfig;
import com.soaresdev.uploaddownloadapi.exceptions.FileDownloadException;
import com.soaresdev.uploaddownloadapi.exceptions.FileInternalErrorException;
import com.soaresdev.uploaddownloadapi.exceptions.FileNotFoundException;
import com.soaresdev.uploaddownloadapi.exceptions.FileUploadException;
import com.soaresdev.uploaddownloadapi.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileSystemUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.matchesRegex;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class FileSystemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileSystemConfig fileSystemConfig;

    private Path uploadDirectory;

    private static final String URL_PATH = "/api/v1/in-system/files";
    private static final String FORM_NAME = "file";
    private static final String VALID_FILE_NAME_ONE = "testing-one.txt";
    private static final String VALID_FILE_NAME_TWO = "testing-two.png";
    private static final String INVALID_FILE_NAME = "te!sting.txt";
    private static final int ONE_MB_IN_BYTES = 1000000;
    private static final byte[] BINARY_DATA_ONE = "testing...".getBytes();
    private static final byte[] BINARY_DATA_TWO = new byte[ONE_MB_IN_BYTES];

    private MockMultipartFile validFile;
    private MockMultipartFile invalidFile;

    @BeforeEach
    void setup() throws IOException {
        uploadDirectory = Paths.get(fileSystemConfig.getUploadDirectory()).toAbsolutePath().normalize();
        cleanup();
        init();
    }

    @Test
    void shouldUploadFileAndReturn200() throws Exception {
        mockMvc.perform(multipart(URL_PATH + "/upload").
                file(validFile)).
                andExpect(status().isOk()).
                andExpect(content().contentType(MediaType.APPLICATION_JSON)).
                andExpect(jsonPath("$.fileName", is(VALID_FILE_NAME_ONE))).
                andExpect(jsonPath("$.fileDownloadUri", containsString(URL_PATH + "/download/" + VALID_FILE_NAME_ONE))).
                andExpect(jsonPath("$.fileType", is(MediaType.TEXT_PLAIN_VALUE))).
                andExpect(jsonPath("$.fileSize", is(FileUtils.humanReadableByteCountSI(BINARY_DATA_ONE.length)))).
                andDo(print());

        assertEquals(1, Files.list(uploadDirectory).count());
    }

    @Test
    void shouldReturn400WhenUploadFileWithInvalidName() throws Exception {
        mockMvc.perform(multipart(URL_PATH + "/upload").
                file(invalidFile)).
                andExpect(status().isBadRequest()).
                andExpect(content().contentType(MediaType.APPLICATION_JSON)).
                andExpect(jsonPath("$.timestamp",
                        matchesRegex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{9}Z"))).
                andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value()))).
                andExpect(jsonPath("$.error",
                        is(FileUploadException.class.getSimpleName()))).
                andExpect(jsonPath("$.message", is("Invalid file name: " + INVALID_FILE_NAME))).
                andExpect(jsonPath("$.path", is(URL_PATH + "/upload"))).
                andDo(print());

        assertEquals(0, Files.list(uploadDirectory).count());
    }

    @Test
    void shouldReturn400WhenFileExistsInUploadFile() throws Exception {
        addFileToUploadDirectory(VALID_FILE_NAME_ONE, BINARY_DATA_ONE);

        mockMvc.perform(multipart(URL_PATH + "/upload").
                file(validFile)).
                andExpect(status().isBadRequest()).
                andExpect(content().contentType(MediaType.APPLICATION_JSON)).
                andExpect(jsonPath("$.timestamp",
                        matchesRegex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{9}Z"))).
                andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value()))).
                andExpect(jsonPath("$.error",
                        is(FileUploadException.class.getSimpleName()))).
                andExpect(jsonPath("$.message", is("File already exists: " + VALID_FILE_NAME_ONE))).
                andExpect(jsonPath("$.path", is(URL_PATH + "/upload"))).
                andDo(print());

        assertEquals(1, Files.list(uploadDirectory).count());
    }

    @Test
    @DirtiesContext
    void shouldReturn500WhenUploadDirectoryNotExistsInUploadFile() throws Exception {
        deleteUploadDirectory();

        mockMvc.perform(multipart(URL_PATH + "/upload").
                file(validFile)).
                andExpect(status().isInternalServerError()).
                andExpect(content().contentType(MediaType.APPLICATION_JSON)).
                andExpect(jsonPath("$.timestamp",
                        matchesRegex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{9}Z"))).
                andExpect(jsonPath("$.status", is(HttpStatus.INTERNAL_SERVER_ERROR.value()))).
                andExpect(jsonPath("$.error",
                        is(FileInternalErrorException.class.getSimpleName()))).
                andExpect(jsonPath("$.message", is("Fatal error. Could not upload file: " + VALID_FILE_NAME_ONE))).
                andExpect(jsonPath("$.path", is(URL_PATH + "/upload"))).
                andDo(print());
    }

    @Test
    void shouldUploadFilesAndReturn200() throws Exception {
        String formName = "files";
        MockMultipartFile validFile = new MockMultipartFile(formName, VALID_FILE_NAME_ONE, MediaType.TEXT_PLAIN_VALUE, BINARY_DATA_ONE);
        MockMultipartFile validFileTwo = new MockMultipartFile(formName, VALID_FILE_NAME_TWO, MediaType.IMAGE_PNG_VALUE, BINARY_DATA_TWO);

        mockMvc.perform(multipart(URL_PATH + "/uploads").
                file(validFile).file(validFileTwo)).
                andExpect(status().isOk()).
                andExpect(content().contentType(MediaType.APPLICATION_JSON)).
                andExpect(jsonPath("$.[0].fileName", is(VALID_FILE_NAME_ONE))).
                andExpect(jsonPath("$.[0].fileDownloadUri", containsString(URL_PATH + "/download/" + VALID_FILE_NAME_ONE))).
                andExpect(jsonPath("$.[0].fileType", is(MediaType.TEXT_PLAIN_VALUE))).
                andExpect(jsonPath("$.[0].fileSize", is(FileUtils.humanReadableByteCountSI(BINARY_DATA_ONE.length)))).
                andExpect(jsonPath("$.[1].fileName", is(VALID_FILE_NAME_TWO))).
                andExpect(jsonPath("$.[1].fileDownloadUri", containsString(URL_PATH + "/download/" + VALID_FILE_NAME_TWO))).
                andExpect(jsonPath("$.[1].fileType", is(MediaType.IMAGE_PNG_VALUE))).
                andExpect(jsonPath("$.[1].fileSize", is(FileUtils.humanReadableByteCountSI(BINARY_DATA_TWO.length)))).
                andDo(print());

        assertEquals(2, Files.list(uploadDirectory).count());
    }

    @Test
    void shouldReturn400WhenUploadFileWithInvalidNameInUploadFiles() throws Exception {
        String formName = "files";
        MockMultipartFile validFile = new MockMultipartFile(formName, VALID_FILE_NAME_ONE, MediaType.TEXT_PLAIN_VALUE, BINARY_DATA_ONE);
        MockMultipartFile invalidFile = new MockMultipartFile(formName, INVALID_FILE_NAME, MediaType.IMAGE_PNG_VALUE, BINARY_DATA_TWO);

        mockMvc.perform(multipart(URL_PATH + "/uploads").
                file(validFile).file(invalidFile)).
                andExpect(status().isBadRequest()).
                andExpect(content().contentType(MediaType.APPLICATION_JSON)).
                andExpect(jsonPath("$.timestamp",
                        matchesRegex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{9}Z"))).
                andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value()))).
                andExpect(jsonPath("$.error",
                        is(FileUploadException.class.getSimpleName()))).
                andExpect(jsonPath("$.message", is("Invalid file name: " + INVALID_FILE_NAME))).
                andExpect(jsonPath("$.path", is(URL_PATH + "/uploads"))).
                andDo(print());

        assertEquals(0, Files.list(uploadDirectory).count());
    }

    @Test
    void shouldReturn400WhenFileExistsInUploadFiles() throws Exception {
        addFileToUploadDirectory(VALID_FILE_NAME_TWO, BINARY_DATA_TWO);
        String formName = "files";
        MockMultipartFile validFile = new MockMultipartFile(formName, VALID_FILE_NAME_ONE, MediaType.TEXT_PLAIN_VALUE, BINARY_DATA_ONE);
        MockMultipartFile validFileTwo = new MockMultipartFile(formName, VALID_FILE_NAME_TWO, MediaType.IMAGE_PNG_VALUE, BINARY_DATA_TWO);

        mockMvc.perform(multipart(URL_PATH + "/uploads").
                file(validFile).file(validFileTwo)).
                andExpect(status().isBadRequest()).
                andExpect(content().contentType(MediaType.APPLICATION_JSON)).
                andExpect(jsonPath("$.timestamp",
                        matchesRegex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{9}Z"))).
                andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value()))).
                andExpect(jsonPath("$.error",
                        is(FileUploadException.class.getSimpleName()))).
                andExpect(jsonPath("$.message", is("File already exists: " + VALID_FILE_NAME_TWO))).
                andExpect(jsonPath("$.path", is(URL_PATH + "/uploads"))).
                andDo(print());

        assertEquals(1, Files.list(uploadDirectory).count());
    }

    @Test
    @DirtiesContext
    void shouldReturn500WhenUploadDirectoryNotExistsInUploadFiles() throws Exception {
        deleteUploadDirectory();
        String formName = "files";
        MockMultipartFile validFile = new MockMultipartFile(formName, VALID_FILE_NAME_ONE, MediaType.TEXT_PLAIN_VALUE, BINARY_DATA_ONE);
        MockMultipartFile validFileTwo = new MockMultipartFile(formName, VALID_FILE_NAME_TWO, MediaType.IMAGE_PNG_VALUE, BINARY_DATA_TWO);

        mockMvc.perform(multipart(URL_PATH + "/uploads").
                file(validFile).file(validFileTwo)).
                andExpect(status().isInternalServerError()).
                andExpect(content().contentType(MediaType.APPLICATION_JSON)).
                andExpect(jsonPath("$.timestamp",
                        matchesRegex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{9}Z"))).
                andExpect(jsonPath("$.status", is(HttpStatus.INTERNAL_SERVER_ERROR.value()))).
                andExpect(jsonPath("$.error",
                        is(FileInternalErrorException.class.getSimpleName()))).
                andExpect(jsonPath("$.message", is("Fatal error. Could not upload file: " + VALID_FILE_NAME_ONE))).
                andExpect(jsonPath("$.path", is(URL_PATH + "/uploads"))).
                andDo(print());
    }

    @Test
    void shouldDownloadFileAndReturn200() throws Exception {
        addFileToUploadDirectory(VALID_FILE_NAME_ONE, BINARY_DATA_ONE);

        mockMvc.perform(get(URL_PATH + "/download/{fileName}", VALID_FILE_NAME_ONE)).
                andExpect(status().isOk()).
                andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE)).
                andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + VALID_FILE_NAME_ONE)).
                andExpect(content().string(new String(BINARY_DATA_ONE))).
                andDo(print());
    }

    @Test
    void shouldReturn400WhenInvalidFileNameInDownloadFile() throws Exception {
        mockMvc.perform(get(URL_PATH + "/download/{fileName}", INVALID_FILE_NAME)).
                andExpect(status().isBadRequest()).
                andExpect(content().contentType(MediaType.APPLICATION_JSON)).
                andExpect(jsonPath("$.timestamp",
                        matchesRegex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{9}Z"))).
                andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value()))).
                andExpect(jsonPath("$.error",
                        is(FileDownloadException.class.getSimpleName()))).
                andExpect(jsonPath("$.message", is("Invalid file name: " + INVALID_FILE_NAME))).
                andExpect(jsonPath("$.path", is(URL_PATH + "/download/" + INVALID_FILE_NAME))).
                andDo(print());
    }

    @Test
    void shouldReturn404WhenFileNotFoundInDownloadFile() throws Exception {
        mockMvc.perform(get(URL_PATH + "/download/{fileName}", VALID_FILE_NAME_ONE)).
                andExpect(status().isNotFound()).
                andExpect(content().contentType(MediaType.APPLICATION_JSON)).
                andExpect(jsonPath("$.timestamp",
                        matchesRegex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{9}Z"))).
                andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value()))).
                andExpect(jsonPath("$.error",
                        is(FileNotFoundException.class.getSimpleName()))).
                andExpect(jsonPath("$.message", is("File not found: " + VALID_FILE_NAME_ONE))).
                andExpect(jsonPath("$.path", is(URL_PATH + "/download/" + VALID_FILE_NAME_ONE))).
                andDo(print());
    }

    @Test
    void shouldListAllFilesAndReturn200() throws Exception {
        addFileToUploadDirectory(VALID_FILE_NAME_ONE, BINARY_DATA_ONE);
        addFileToUploadDirectory(VALID_FILE_NAME_TWO, BINARY_DATA_TWO);

        mockMvc.perform(get(URL_PATH)).
                andExpect(status().isOk()).
                andExpect(content().contentType(MediaType.APPLICATION_JSON)).
                andExpect(jsonPath("$.[0].fileName", is(VALID_FILE_NAME_ONE))).
                andExpect(jsonPath("$.[0].fileDownloadUri", containsString(URL_PATH + "/download/" + VALID_FILE_NAME_ONE))).
                andExpect(jsonPath("$.[0].fileType", is(MediaType.TEXT_PLAIN_VALUE))).
                andExpect(jsonPath("$.[0].fileSize", is(FileUtils.humanReadableByteCountSI(BINARY_DATA_ONE.length)))).
                andExpect(jsonPath("$.[1].fileName", is(VALID_FILE_NAME_TWO))).
                andExpect(jsonPath("$.[1].fileDownloadUri", containsString(URL_PATH + "/download/" + VALID_FILE_NAME_TWO))).
                andExpect(jsonPath("$.[1].fileType", is(MediaType.IMAGE_PNG_VALUE))).
                andExpect(jsonPath("$.[1].fileSize", is(FileUtils.humanReadableByteCountSI(BINARY_DATA_TWO.length)))).
                andDo(print());
    }

    @Test
    @DirtiesContext
    void shouldReturn500WhenUploadDirectoryNotExistsInListAllFiles() throws Exception {
        deleteUploadDirectory();

        mockMvc.perform(get(URL_PATH)).
                andExpect(status().isInternalServerError()).
                andExpect(content().contentType(MediaType.APPLICATION_JSON)).
                andExpect(jsonPath("$.timestamp",
                        matchesRegex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{9}Z"))).
                andExpect(jsonPath("$.status", is(HttpStatus.INTERNAL_SERVER_ERROR.value()))).
                andExpect(jsonPath("$.error",
                        is(FileInternalErrorException.class.getSimpleName()))).
                andExpect(jsonPath("$.message", is("Fatal error. Could not list all files"))).
                andExpect(jsonPath("$.path", is(URL_PATH))).
                andDo(print());
    }

    private void init() {
        validFile = new MockMultipartFile(FORM_NAME, VALID_FILE_NAME_ONE, MediaType.TEXT_PLAIN_VALUE, BINARY_DATA_ONE);
        invalidFile = new MockMultipartFile(FORM_NAME, INVALID_FILE_NAME, MediaType.TEXT_PLAIN_VALUE, BINARY_DATA_ONE);
    }

    private void cleanup() throws IOException {
        if (Files.exists(uploadDirectory)) {
            Files.walk(uploadDirectory)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    private void addFileToUploadDirectory(String fileName, byte[] binaryData) throws IOException {
        Path filePath = uploadDirectory.resolve(fileName).normalize();
        Files.write(filePath, binaryData);
    }

    private void deleteUploadDirectory() throws Exception {
        FileSystemUtils.deleteRecursively(uploadDirectory);
    }
}
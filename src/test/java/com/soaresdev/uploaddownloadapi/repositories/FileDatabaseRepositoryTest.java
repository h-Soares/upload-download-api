package com.soaresdev.uploaddownloadapi.repositories;

import com.soaresdev.uploaddownloadapi.entities.FileDatabaseEntity;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.MediaType;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileDatabaseRepositoryTest {
    @Autowired
    private FileDatabaseRepository fileDatabaseRepository;

    private static final String IN_DB_FILE_NAME = "testing.txt";
    private static final String NOT_IN_DB_FILE_NAME = "other-text.txt";
    private static final long ONE_MB_IN_BYTES = 1000000;
    private static final byte[] BINARY_DATA = "Testing...".getBytes();

    @BeforeAll
    void setup() {
        fileDatabaseRepository.save(new FileDatabaseEntity(IN_DB_FILE_NAME, MediaType.TEXT_PLAIN_VALUE, ONE_MB_IN_BYTES, BINARY_DATA));
    }

    @Test
    void shouldFindFileDatabaseEntityByFileName() {
        Optional<FileDatabaseEntity> file = fileDatabaseRepository.findByFileName(IN_DB_FILE_NAME);

        assertTrue(file.isPresent());
        assertEquals(IN_DB_FILE_NAME, file.get().getFileName());
        assertEquals(MediaType.TEXT_PLAIN_VALUE, file.get().getFileType());
        assertEquals(ONE_MB_IN_BYTES, file.get().getFileByteSize());
        assertArrayEquals(BINARY_DATA, file.get().getBinaryData());
    }

    @Test
    void shouldNotFindFileDatabaseEntityByFileName() {
        Optional<FileDatabaseEntity> file = fileDatabaseRepository.findByFileName(NOT_IN_DB_FILE_NAME);

        assertFalse(file.isPresent());
    }

    @Test
    void shouldFileDatabaseEntityExistsByFileName() {
        assertTrue(fileDatabaseRepository.existsByFileName(IN_DB_FILE_NAME));
    }
    
    @Test
    void shouldNotFileDatabaseEntityExistsByFileName() {
        assertFalse(fileDatabaseRepository.existsByFileName(NOT_IN_DB_FILE_NAME));
    }
}
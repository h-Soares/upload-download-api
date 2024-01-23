package com.soaresdev.uploaddownloadapi.repositories;

import com.soaresdev.uploaddownloadapi.entities.FileDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileDatabaseRepository extends JpaRepository<FileDatabase, UUID> {
    Optional<FileDatabase> findFileDatabaseByFileName(String fileName);
}
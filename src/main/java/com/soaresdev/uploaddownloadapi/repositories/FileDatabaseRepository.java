package com.soaresdev.uploaddownloadapi.repositories;

import com.soaresdev.uploaddownloadapi.entities.FileDatabaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileDatabaseRepository extends JpaRepository<FileDatabaseEntity, UUID> {
    Optional<FileDatabaseEntity> findByFileName(String fileName);

    boolean existsByFileName(String fileName);
}
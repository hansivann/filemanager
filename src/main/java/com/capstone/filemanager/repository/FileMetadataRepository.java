package com.capstone.filemanager.repository;

import com.capstone.filemanager.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByFileNameContainingIgnoreCase(String fileName);
    List<FileMetadata> findByFolderIgnoreCase(String folder);
}

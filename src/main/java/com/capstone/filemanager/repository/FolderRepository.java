package com.capstone.filemanager.repository;

import com.capstone.filemanager.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    Optional<Folder> findByName(String name);

}

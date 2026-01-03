package com.capstone.filemanager.controller;

import com.capstone.filemanager.entity.Folder;
import com.capstone.filemanager.repository.FolderRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/folders")
@CrossOrigin(origins = "*")
public class FolderController {

    private final FolderRepository folderRepository;

    public FolderController(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    @GetMapping
    public List<Folder> getAllFolders() {
        return folderRepository.findAll();
    }

    @PostMapping
    public Folder createFolder(@RequestParam String name) {
        return folderRepository.save(new Folder(name));
    }

    @DeleteMapping("/{name}")
    public void deleteFolder(@PathVariable String name) {
        folderRepository.findByName(name)
                .ifPresent(folderRepository::delete);
    }
}

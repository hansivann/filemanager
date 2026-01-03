package com.capstone.filemanager.controller;


import com.capstone.filemanager.entity.FileMetadata;
import com.capstone.filemanager.entity.Folder;
import com.capstone.filemanager.service.FileService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "*")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService){
        this.fileService = fileService;
    }

    //UPLOADING A FILE
    @PostMapping("/upload")
    public FileMetadata upload(@RequestParam("file") MultipartFile file,
//                               @RequestParam(defaultValue = "root") String folder
                               @RequestParam String folder
                               ) throws IOException {
        return fileService.upload(file, folder);
    }

//    these work now when GET: /files, /files?name=, /files?name=pdf
    @GetMapping
    public List<FileMetadata> getFiles(
            @RequestParam(required = false) String name) {

        if (name == null || name.isBlank()) {
            return fileService.findAll();
        }
        return fileService.search(name);
    }
    // something like= :8080/files/folder/docs
    // in this case docs is the name of the folder
    @GetMapping("/folder/{folder}")
    public List<FileMetadata> getByFolder(@PathVariable String folder) {
        return fileService.findByFolder(folder);
    }

    //this will print the report into a csv file
    @GetMapping("/report")
    public ResponseEntity<String> generateReport(){
        String csv = fileService.generateReport();

        return ResponseEntity.ok()
                .header("Content-Disposition","attachment; filename=file-report.csv")
                .header("Content-Type", "text/csv")
                .body(csv);
    }

    @GetMapping("/search")
    public List<FileMetadata> search(@RequestParam String query){
        return fileService.searchFiles(query);
    }

    //download file
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try{
            FileMetadata file = fileService.findById(id);
            Resource resource = fileService.getFileAsResource(id);

            return ResponseEntity.ok().contentType(MediaType.parseMediaType(file.getFileType())).header("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"").body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //delete file
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        fileService.delete(id);
    }

    //delete folder
    @DeleteMapping("/folder/{folderName}")
    public void deleteFolder(@PathVariable String folderName) throws IOException {
        fileService.deleteFolder(folderName);
    }

    //create folder
    @PostMapping("/folder/{folderName}")
    public Folder createFolder(@PathVariable String name) throws IOException {
        return fileService.createFolder(name);
    }

    //get folders
    @GetMapping("/folders")
    public List<String> getAllFolders() {
        return fileService.getAllFolders();
    }

    //move to another folder. need to Body to be raw JSON
//    {
//        "newFolder": "target-folder-name"
//    }
    @PutMapping("/{id}/move")
    public FileMetadata move(
            @PathVariable Long id,
            @Valid @RequestBody MoveFileRequest request
    ) throws IOException {
        return fileService.moveFile(id, request.getNewFolder());
    }
}

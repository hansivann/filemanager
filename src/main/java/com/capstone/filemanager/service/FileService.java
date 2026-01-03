package com.capstone.filemanager.service;

import com.capstone.filemanager.entity.FileMetadata;
import com.capstone.filemanager.entity.Folder;
import com.capstone.filemanager.repository.FileMetadataRepository;
import com.capstone.filemanager.repository.FolderRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FileService {

    private final FileMetadataRepository repository;
//    private final Path baseUploadDir;
    private final FolderRepository folderRepository;
    private final String uploadDir;
    private Path baseUploadDir;

    public FileService(
            FileMetadataRepository repository,
            FolderRepository folderRepository,
            @Value("${file.upload-dir}") String uploadDir
    ) throws IOException {
        this.repository = repository;
        this.folderRepository = folderRepository;
        this.uploadDir = uploadDir;
//        this.baseUploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
//        Files.createDirectories(this.baseUploadDir);
    }
    @PostConstruct
    public void init() throws IOException{
        this.baseUploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.baseUploadDir);
    }

    List<String> allowedTypes = List.of(
            "application/pdf", "image/png", "image/jpeg"
    );

    public FileMetadata upload(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            throw new IllegalArgumentException("Invalid file name");
        }

        if (!allowedTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported file type");
        }

        // default folder if blank
//        if (folder == null || folder.isBlank()) {
//            folder = "root";
//        }

        if (folder == null || folder.isBlank()) {
            throw new IllegalArgumentException("Folder is required");
        }

        Path folderPath = baseUploadDir.resolve(folder).normalize();
        Files.createDirectories(folderPath);

        String storedName = System.currentTimeMillis() + "_" + originalName;
        Path destination = folderPath.resolve(storedName);

        file.transferTo(destination.toFile());

        FileMetadata metadata = new FileMetadata(
                originalName,
                file.getContentType(),
                file.getSize(),
                destination.toString(),
                folder
        );

        return repository.save(metadata);
    }

    public FileMetadata moveFile(Long id, String newFolder) throws IOException {
        if (newFolder == null || newFolder.isBlank()) {
            throw new IllegalArgumentException("newFolder is required");
        }

        FileMetadata file = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        Path currentPath = Paths.get(file.getStoragePath());

        Path newFolderPath = baseUploadDir.resolve(newFolder).normalize();
        Files.createDirectories(newFolderPath);

        Path newPath = newFolderPath.resolve(currentPath.getFileName());
        Files.move(currentPath, newPath);

        file.setStoragePath(newPath.toString());
        file.setFolder(newFolder);

        return repository.save(file);
    }

    public List<FileMetadata> search(String name) {
        return repository.findByFileNameContainingIgnoreCase(name);
    }

    //gonna use this in the Search function
    public List<FileMetadata> searchFiles(String query){
        return repository.findByFileNameContainingIgnoreCase(query);
    }

    public List<FileMetadata> findByFolder(String folder){
        return repository.findByFolderIgnoreCase(folder);
    }

    public List<FileMetadata> findAll() {
        return repository.findAll();
    }

    public void delete(Long id) {
        repository.findById(id).ifPresent(file -> {
            try {
                Files.deleteIfExists(Paths.get(file.getStoragePath()));
            } catch (IOException ignored) {}
            repository.delete(file);
        });
    }

    //generate a csv report
    public String generateReport(){
        List<FileMetadata> files = repository.findAll();

        StringBuilder csv = new StringBuilder();

        csv.append("File management report\n");
        csv.append("Generated at: ").append(LocalDateTime.now()).append("\n\n");

        csv.append("ID, File name, File type, Size (bytes), Folder, Uploaded at\n");

        for (FileMetadata file : files) {
            csv.append(file.getId()).append(",")
                    .append(file.getFileName()).append(",")
                    .append(file.getFileType()).append(",")
                    .append(file.getSize()).append(",")
                    .append(file.getFolder()).append(",")
                    .append(file.getUploadedAt()).append("\n");
        }

        return csv.toString();

    }

    public void deleteFolder(String folderName) throws IOException {
        if (folderName == null || folderName.isBlank()) {
            throw new IllegalArgumentException("Folder name is required");
        }

        List<FileMetadata> filesInFolder = repository.findByFolderIgnoreCase(folderName);

        // Delete all files in the folder
        for (FileMetadata file : filesInFolder) {
            try {
                Files.deleteIfExists(Paths.get(file.getStoragePath()));
            } catch (IOException ignored) {}
            repository.delete(file);
        }

        // Try to delete the folder directory if it's empty
        Path folderPath = baseUploadDir.resolve(folderName).normalize();
        try {
            Files.deleteIfExists(folderPath);
        } catch (IOException ignored) {
            // Folder might not be empty or other issues, ignore
        }
    }

    public Folder createFolder(String folderName) throws IOException {
        if (folderName == null || folderName.isBlank()) {
            throw new IllegalArgumentException("Folder name is required");
        }

        Path folderPath = baseUploadDir.resolve(folderName).normalize();
        Files.createDirectories(folderPath);

        return folderRepository.findByName(folderName)
                .orElseGet(() -> folderRepository.save(new Folder(folderName)));
    }

    public List<String> getAllFolders() {
        List<FileMetadata> allFiles = repository.findAll();
        return allFiles.stream()
                .map(FileMetadata::getFolder)
                .distinct()
                .toList();
    }

    public FileMetadata findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("File not found"));
    }

    public Resource getFileAsResource(Long id){
        FileMetadata file = findById(id);
        return new FileSystemResource(Paths.get(file.getStoragePath()));
    }

}



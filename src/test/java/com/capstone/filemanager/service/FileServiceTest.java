package com.capstone.filemanager.service;

import com.capstone.filemanager.entity.FileMetadata;
import com.capstone.filemanager.repository.FileMetadataRepository;
import com.capstone.filemanager.repository.FolderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileMetadataRepository repository;

    @InjectMocks
    private FileService fileService;
    private FolderRepository folderRepository;


    @Test
    void searchFiles_returnPartialMatches(){
        FileMetadata file = new FileMetadata();
        file.setFileName("report.pdf");

        when(repository.findByFileNameContainingIgnoreCase("rep")).thenReturn(List.of(file));

        var result = fileService.searchFiles("rep");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFileName()).isEqualTo("report.pdf");
    }

    @BeforeEach
    void setUp() throws IOException{
        fileService = new FileService(
                repository,
                folderRepository,
                "test-uploads"
        );
    }

    @Test
    void searchFiles_withPartialName_returnsMultipleResults() {
        FileMetadata f1 = new FileMetadata();
        f1.setFileName("report.pdf");

        FileMetadata f2 = new FileMetadata();
        f2.setFileName("report_final.pdf");

        when(repository.findByFileNameContainingIgnoreCase("report"))
                .thenReturn(List.of(f1, f2));

        List<FileMetadata> results = fileService.searchFiles("report");

        assertEquals(2, results.size());
        assertTrue(results.stream()
                .allMatch(file -> file.getFileName().contains("report")));
    }


}

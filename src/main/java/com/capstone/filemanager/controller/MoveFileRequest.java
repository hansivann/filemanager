package com.capstone.filemanager.controller;

import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
public class MoveFileRequest {
    private String newFolder;

    public String getNewFolder() {
        return newFolder;
    }

    public void setNewFolder(String newFolder){
        this.newFolder = newFolder;
    }
}

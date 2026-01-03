package com.capstone.filemanager.service;

import com.capstone.filemanager.entity.User;

public interface UserService {
    User register(String username, String password);
}

package com.capstone.filemanager.service;

import com.capstone.filemanager.entity.User;
import com.capstone.filemanager.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

//    @Override
    public User register(String username, String password){
        User user = new User(username, password);
        return repository.save(user);
    }
}

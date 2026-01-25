package com.capstone.filemanager.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {


    // public AuthController(UserService userService) {
    // }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password){
        return "User registered successfully";
    }
}

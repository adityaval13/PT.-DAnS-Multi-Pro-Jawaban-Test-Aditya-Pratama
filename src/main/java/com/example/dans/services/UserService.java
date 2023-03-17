package com.example.dans.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dans.models.entities.User;
import com.example.dans.models.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public User validateUser(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }
}


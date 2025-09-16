package com.healthmate.backendv2.user.service;

import org.springframework.stereotype.Service;

import java.util.Optional;

import com.healthmate.backendv2.user.entity.User;
import com.healthmate.backendv2.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}



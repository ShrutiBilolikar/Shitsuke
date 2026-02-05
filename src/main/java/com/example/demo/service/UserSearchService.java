package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserSearchService {

    private final UserRepository userRepository;

    public UserSearchService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> searchUsers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return userRepository.searchByUsername(searchTerm.trim());
    }
}
package com.example.demo.dto;

import com.example.demo.model.User;

public record UserSearchResultDto(
        String userId,
        String username,
        String email
) {
    public static UserSearchResultDto fromEntity(User user) {
        return new UserSearchResultDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
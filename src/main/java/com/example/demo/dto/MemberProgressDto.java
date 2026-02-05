package com.example.demo.dto;

import com.example.demo.model.MembershipRole;

public record MemberProgressDto(
        String userId,
        String email,
        String username,
        boolean hasLogged,
        String recordValue,
        MembershipRole role
) {}
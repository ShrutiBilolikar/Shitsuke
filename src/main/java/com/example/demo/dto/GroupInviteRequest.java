package com.example.demo.dto;

import java.util.List;

public record GroupInviteRequest(
        List<String> userEmails
) {
}

package com.example.demo.dto;

import com.example.demo.model.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
public record UserGroupDto(
        String groupId,
        String name,
        String description,
        String creatorId,
        String creatorEmail,
        String creatorUsername,
        String recordTypeId,
        String recordTypeName,
        CompletionRule completionRule,
        Integer customPercentage,
        GroupStatus status,
        Long activeMemberCount,
        Instant createdAt
) {
    public static UserGroupDto fromEntity(UserGroup group, Long memberCount) {
        return new UserGroupDto(
                group.getGroupId(),
                group.getName(),
                group.getDescription(),
                group.getCreator().getId(),
                group.getCreator().getEmail(),
                group.getCreator().getUsername(),
                group.getRecordType().getRecordTypeId(),
                group.getRecordType().getName(),
                group.getCompletionRule(),
                group.getCustomPercentage(),
                group.getStatus(),
                memberCount,
                group.getCreatedAt()
        );
    }
}
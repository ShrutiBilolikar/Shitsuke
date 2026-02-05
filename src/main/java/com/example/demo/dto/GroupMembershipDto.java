package com.example.demo.dto;

import com.example.demo.model.GroupMembership;
import com.example.demo.model.MembershipRole;
import com.example.demo.model.MembershipStatus;

import java.time.Instant;

public record GroupMembershipDto(
        String membershipId,
        String groupId,
        String groupName,
        String userId,
        String userEmail,
        String username,
        MembershipStatus status,
        MembershipRole role,
        Instant invitedAt,
        Instant joinedAt,
        Instant leftAt
) {
    public static GroupMembershipDto fromEntity(GroupMembership membership) {
        return new GroupMembershipDto(
                membership.getMembershipId(),
                membership.getGroup().getGroupId(),
                membership.getGroup().getName(),
                membership.getUser().getId(),
                membership.getUser().getEmail(),
                membership.getUser().getUsername(),
                membership.getStatus(),
                membership.getRole(),
                membership.getInvitedAt(),
                membership.getJoinedAt(),
                membership.getLeftAt()
        );
    }
}
package com.example.demo.dto;
import com.example.demo.model.Friendship;
import com.example.demo.model.FriendshipStatus;

import java.time.Instant;
public record FriendshipDto(
        String friendshipId,
        String userId,
        String friendId,
        String userEmail,
        String friendEmail,
        String username,
        String friendUsername,
        FriendshipStatus status,
        Instant createdAt,
        Instant acceptedAt
) {
    public static FriendshipDto fromEntity(Friendship friendship) {
        return new FriendshipDto(
                friendship.getFriendshipId(),
                friendship.getUser().getId(),
                friendship.getFriend().getId(),
                friendship.getUser().getEmail(),
                friendship.getFriend().getEmail(),
                friendship.getUser().getUsername(),
                friendship.getFriend().getUsername(),
                friendship.getStatus(),
                friendship.getCreatedAt(),
                friendship.getAcceptedAt()
        );
    }
}

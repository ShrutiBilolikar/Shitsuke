package com.example.demo.controller;

import com.example.demo.dto.FriendRequestDto;
import com.example.demo.dto.FriendshipDto;
import com.example.demo.dto.UserSearchResultDto;
import com.example.demo.model.Friendship;
import com.example.demo.model.User;
import com.example.demo.service.FriendshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/friends")
public class FriendshipController {
    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }
    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(
            @RequestBody FriendRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(403).body("Authentication required");
        }
        try {
            Friendship friendship = friendshipService.sendFriendRequest(
                    userDetails.getUsername(),
                    request.recipientEmail()
            );
            return ResponseEntity.ok(FriendshipDto.fromEntity(friendship));
        } catch (IllegalArgumentException e) {
            // User-friendly errors: user not found, cannot send to self, already exists
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            // Other errors (like sender not found - should not happen if authenticated)
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/accept/{friendshipId}")
    public ResponseEntity<FriendshipDto> acceptFriendRequest(
            @PathVariable String friendshipId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Friendship friendship = friendshipService.acceptFriendRequest(
                friendshipId,
                userDetails.getUsername()
        );
        return ResponseEntity.ok(FriendshipDto.fromEntity(friendship));
    }

    @PostMapping("/reject/{friendshipId}")
    public ResponseEntity<Void> rejectFriendRequest(
            @PathVariable String friendshipId,
            @AuthenticationPrincipal UserDetails userDetails) {
        friendshipService.rejectFriendRequest(friendshipId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<FriendshipDto>> getFriends(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            System.out.println("FriendshipController: userDetails is null - authentication failed");
            return ResponseEntity.status(403).body(null);
        }
        List<Friendship> friendships = friendshipService.getFriendships(userDetails.getUsername());
        List<FriendshipDto> friendDtos = friendships.stream()
                .map(FriendshipDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(friendDtos);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FriendshipDto>> getPendingRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(403).build();
        }
        List<Friendship> pending = friendshipService.getPendingRequests(userDetails.getUsername());
        List<FriendshipDto> pendingDtos = pending.stream()
                .map(FriendshipDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pendingDtos);
    }

    @GetMapping("/sent")
    public ResponseEntity<List<FriendshipDto>> getSentRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(403).build();
        }
        List<Friendship> sent = friendshipService.getSentRequests(userDetails.getUsername());
        List<FriendshipDto> sentDtos = sent.stream()
                .map(FriendshipDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sentDtos);
    }

    @DeleteMapping("/{friendshipId}")
    public ResponseEntity<Void> removeFriendship(
            @PathVariable String friendshipId,
            @AuthenticationPrincipal UserDetails userDetails) {
        friendshipService.removeFriendship(friendshipId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}

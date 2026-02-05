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
    public ResponseEntity<FriendshipDto> sendFriendRequest(
            @RequestBody FriendRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Friendship friendship = friendshipService.sendFriendRequest(
                userDetails.getUsername(),
                request.recipientEmail()
        );
        return ResponseEntity.ok(FriendshipDto.fromEntity(friendship));
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
}

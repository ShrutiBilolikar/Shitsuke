package com.example.demo.service;

import com.example.demo.model.Friendship;
import com.example.demo.model.FriendshipStatus;
import com.example.demo.model.User;
import com.example.demo.repo.FriendshipRepository;
import com.example.demo.repo.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    public FriendshipService(FriendshipRepository friendshipRepository,UserRepository userRepository){
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }
    @Transactional
    public Friendship sendFriendRequest(String senderEmail, String recipentEmail){
        if(senderEmail.equals(recipentEmail)){
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }
        User sender = userRepository.findByEmail(senderEmail).orElseThrow(()-> new RuntimeException("Sender not found"));
        User recipent = userRepository.findByEmail(recipentEmail).orElseThrow(()->new RuntimeException("Recipent not found"));
        Optional<Friendship>existing = friendshipRepository.findFriendshipBetween(senderEmail,recipentEmail);
        if(existing.isPresent()){
            throw new IllegalArgumentException("Friend request already send or friendship already exists");
        }
        Friendship friendship = new Friendship();
        friendship.setUser(sender);
        friendship.setFriend(recipent);
        friendship.setStatus(FriendshipStatus.PENDING);
        return friendship;
    }
    @Transactional
    public Friendship acceptFriendRequest(String friendshipId, String recipientEmail){
        Friendship friendship = friendshipRepository.findById(friendshipId).orElseThrow(()->new RuntimeException("Friend request not forund"));
        if (!friendship.getFriend().getEmail().equals(recipientEmail)) {
            throw new SecurityException("You can only accept friend requests sent to you");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Friend request is not pending");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendship.setAcceptedAt(Instant.now());

        return friendshipRepository.save(friendship);

    }
    @Transactional
    public void rejectFriendRequest(String friendshipId, String recipientEmail) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        // Verify the current user is the recipient
        if (!friendship.getFriend().getEmail().equals(recipientEmail)) {
            throw new SecurityException("You can only reject friend requests sent to you");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Friend request is not pending");
        }

        friendship.setStatus(FriendshipStatus.REJECTED);
        friendshipRepository.save(friendship);
    }

    public List<User> getFriends(String userEmail){
        List<Friendship>friendships = friendshipRepository.findAcceptedFriendshipsForUser(userEmail);
        return friendships.stream().map(f->f.getUser().getEmail().equals(userEmail)?f.getFriend() : f.getUser()).collect(Collectors.toList());
    }
    public List<Friendship>getPendingRequests(String userEmail){
        return friendshipRepository.findPendingRequestsForUser(userEmail);
    }
    public List<Friendship> getSentRequests(String userEmail) {
        return friendshipRepository.findSentPendingRequests(userEmail);
    }

    @Transactional
    public void removeFriendship(String friendshipId, String userEmail) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friendship not found"));

        // Verify user is part of this friendship
        if (!friendship.getUser().getEmail().equals(userEmail) &&
                !friendship.getFriend().getEmail().equals(userEmail)) {
            throw new SecurityException("You are not part of this friendship");
        }

        friendshipRepository.delete(friendship);
    }
}

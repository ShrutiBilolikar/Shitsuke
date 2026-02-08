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
    private final EmailService emailService;
    
    public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository, EmailService emailService){
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
    @Transactional
    public Friendship sendFriendRequest(String senderEmail, String recipentEmail){
        if(senderEmail.equals(recipentEmail)){
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }
        User sender = userRepository.findByEmail(senderEmail)
            .orElseThrow(() -> new RuntimeException("Sender not found: " + senderEmail));
        User recipent = userRepository.findByEmail(recipentEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + recipentEmail + ". Please make sure they have registered an account."));
        Optional<Friendship>existing = friendshipRepository.findFriendshipBetween(senderEmail,recipentEmail);
        if(existing.isPresent()){
            throw new IllegalArgumentException("Friend request already send or friendship already exists");
        }
        Friendship friendship = new Friendship();
        friendship.setUser(sender);
        friendship.setFriend(recipent);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendship.setCreatedAt(Instant.now());
        Friendship savedFriendship = friendshipRepository.save(friendship);
        
        // Send email notification to recipient
        System.out.println("FriendshipService: Friend request saved, attempting to send email...");
        System.out.println("FriendshipService: Recipient email: " + recipent.getEmail());
        System.out.println("FriendshipService: Sender email: " + sender.getEmail());
        
        try {
            String senderName = sender.getUsername() != null ? sender.getUsername() : sender.getEmail();
            System.out.println("FriendshipService: Calling emailService.sendFriendRequestEmail()...");
            emailService.sendFriendRequestEmail(recipent.getEmail(), sender.getEmail(), senderName);
            System.out.println("FriendshipService: ✓ Email service call completed successfully");
        } catch (Exception e) {
            // Log error but don't fail the request - email is optional
            System.err.println("FriendshipService: ✗✗✗ FAILED to send friend request email!");
            System.err.println("FriendshipService: Error type: " + e.getClass().getName());
            System.err.println("FriendshipService: Error message: " + e.getMessage());
            System.err.println("FriendshipService: Stack trace:");
            e.printStackTrace();
        }
        
        return savedFriendship;
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

        Friendship savedFriendship = friendshipRepository.save(friendship);
        
        // Send email notification to the original sender
        System.out.println("FriendshipService: Friend request accepted, attempting to send email...");
        try {
            User sender = friendship.getUser();
            String accepterName = friendship.getFriend().getUsername() != null 
                ? friendship.getFriend().getUsername() 
                : friendship.getFriend().getEmail();
            System.out.println("FriendshipService: Calling emailService.sendFriendRequestAcceptedEmail()...");
            emailService.sendFriendRequestAcceptedEmail(sender.getEmail(), friendship.getFriend().getEmail(), accepterName);
            System.out.println("FriendshipService: ✓ Email service call completed successfully");
        } catch (Exception e) {
            // Log error but don't fail the request - email is optional
            System.err.println("FriendshipService: ✗✗✗ FAILED to send friend request accepted email!");
            System.err.println("FriendshipService: Error type: " + e.getClass().getName());
            System.err.println("FriendshipService: Error message: " + e.getMessage());
            e.printStackTrace();
        }
        
        return savedFriendship;

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
    
    public List<Friendship> getFriendships(String userEmail){
        return friendshipRepository.findAcceptedFriendshipsForUser(userEmail);
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

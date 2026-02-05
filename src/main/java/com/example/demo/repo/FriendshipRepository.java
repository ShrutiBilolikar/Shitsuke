package com.example.demo.repo;

import com.example.demo.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship,String> {
    @Query("SELECT f FROM Friendship f WHERE f.friend.email = :email AND f.status = 'PENDING'")
    List<Friendship> findPendingRequestsForUser(@Param("email")String email);
    @Query("SELECT f FROM Friendship f WHERE (f.user.email = :email OR f.friend.email = :email) AND f.status = 'ACCEPTED'")
    List<Friendship> findAcceptedFriendshipsForUser(@Param("email") String email);

    // Check if friendship exists between two users (either direction)
    @Query("SELECT f FROM Friendship f WHERE " +
            "((f.user.email = :userEmail AND f.friend.email = :friendEmail) OR " +
            "(f.user.email = :friendEmail AND f.friend.email = :userEmail))")
    Optional<Friendship> findFriendshipBetween(
            @Param("userEmail") String userEmail,
            @Param("friendEmail") String friendEmail
    );

    // Check if users are friends (ACCEPTED status, either direction)
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friendship f WHERE " +
            "((f.user.email = :userEmail AND f.friend.email = :friendEmail) OR " +
            "(f.user.email = :friendEmail AND f.friend.email = :userEmail)) " +
            "AND f.status = 'ACCEPTED'")
    boolean areUsersFriends(
            @Param("userEmail") String userEmail,
            @Param("friendEmail") String friendEmail
    );

    // Find sent pending requests
    @Query("SELECT f FROM Friendship f WHERE f.user.email = :email AND f.status = 'PENDING'")
    List<Friendship> findSentPendingRequests(@Param("email") String email);
}

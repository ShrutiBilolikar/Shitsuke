package com.example.demo.controller;

import com.example.demo.dto.GroupStreakDto;
import com.example.demo.dto.StreakDto;
import com.example.demo.service.StreakService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/streaks")
public class StreakController {

    private final StreakService streakService;

    public StreakController(StreakService streakService) {
        this.streakService = streakService;
    }

    /**
     * Get user's streak for a specific record type
     * GET /api/streaks/user/{recordTypeId}
     */
    @GetMapping("/user/{recordTypeId}")
    public ResponseEntity<StreakDto> getUserStreak(
            @PathVariable String recordTypeId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        StreakDto streak = streakService.calculateUserStreak(recordTypeId, userDetails.getUsername());
        return ResponseEntity.ok(streak);
    }

    /**
     * Get group's streak
     * GET /api/streaks/group/{groupId}
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<GroupStreakDto> getGroupStreak(
            @PathVariable String groupId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        GroupStreakDto streak = streakService.calculateGroupStreak(groupId, userDetails.getUsername());
        return ResponseEntity.ok(streak);
    }
}

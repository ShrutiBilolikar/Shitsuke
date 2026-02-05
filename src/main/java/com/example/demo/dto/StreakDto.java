package com.example.demo.dto;

import java.time.LocalDate;

public record StreakDto(
        String recordTypeId,
        String recordTypeName,
        int currentStreak,
        int longestStreak,
        LocalDate streakStartDate,
        LocalDate lastLoggedDate,
        boolean isActive
) {
    public static StreakDto of(
            String recordTypeId,
            String recordTypeName,
            int currentStreak,
            int longestStreak,
            LocalDate streakStartDate,
            LocalDate lastLoggedDate,
            boolean isActive
    ) {
        return new StreakDto(
                recordTypeId,
                recordTypeName,
                currentStreak,
                longestStreak,
                streakStartDate,
                lastLoggedDate,
                isActive
        );
    }
}

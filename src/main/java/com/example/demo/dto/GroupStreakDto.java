package com.example.demo.dto;

import java.time.LocalDate;

public record GroupStreakDto(
        String groupId,
        String groupName,
        String recordTypeId,
        String recordTypeName,
        int currentStreak,
        int longestStreak,
        LocalDate streakStartDate,
        LocalDate lastCompletedDate,
        boolean isActive
) {
    public static GroupStreakDto of(
            String groupId,
            String groupName,
            String recordTypeId,
            String recordTypeName,
            int currentStreak,
            int longestStreak,
            LocalDate streakStartDate,
            LocalDate lastCompletedDate,
            boolean isActive
    ) {
        return new GroupStreakDto(
                groupId,
                groupName,
                recordTypeId,
                recordTypeName,
                currentStreak,
                longestStreak,
                streakStartDate,
                lastCompletedDate,
                isActive
        );
    }
}

package com.example.demo.service;

import com.example.demo.dto.GroupStreakDto;
import com.example.demo.dto.StreakDto;
import com.example.demo.model.*;
import com.example.demo.repo.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StreakService {

    private final RecordRepository recordRepository;
    private final RecordTypeRepository recordTypeRepository;
    private final UserGroupRepository groupRepository;
    private final GroupMembershipRepository membershipRepository;
    private final GroupDailyProgressService progressService;

    public StreakService(
            RecordRepository recordRepository,
            RecordTypeRepository recordTypeRepository,
            UserGroupRepository groupRepository,
            GroupMembershipRepository membershipRepository,
            GroupDailyProgressService progressService
    ) {
        this.recordRepository = recordRepository;
        this.recordTypeRepository = recordTypeRepository;
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.progressService = progressService;
    }

    /**
     * Calculate current and longest streak for a user's record type
     */
    public StreakDto calculateUserStreak(String recordTypeId, String userEmail) {
        RecordType recordType = recordTypeRepository.findById(recordTypeId)
                .orElseThrow(() -> new RuntimeException("RecordType not found"));

        // Get all records for this user and record type, ordered by date descending
        List<com.example.demo.model.Record> records = recordRepository
                .findByUserEmailAndRecordTypeIdOrderByRecordDateDesc(userEmail, recordTypeId);

        if (records.isEmpty()) {
            return StreakDto.of(
                    recordTypeId,
                    recordType.getName(),
                    0,
                    0,
                    null,
                    null,
                    false
            );
        }

        // Calculate current streak (from today backwards)
        int currentStreak = 0;
        LocalDate today = LocalDate.now();
        LocalDate checkDate = today;
        LocalDate streakStartDate = null;
        LocalDate lastLoggedDate = records.get(0).getRecordDate();

        // Convert records to a set of dates for quick lookup
        Set<LocalDate> recordDates = records.stream()
                .map(com.example.demo.model.Record::getRecordDate)
                .collect(Collectors.toSet());

        // Check if today or yesterday has a record (streak is still active)
        boolean isActive = recordDates.contains(today) || recordDates.contains(today.minusDays(1));

        // Calculate current streak
        // Start from today if there's a record, otherwise from yesterday
        if (!recordDates.contains(today)) {
            checkDate = today.minusDays(1);
        }

        while (recordDates.contains(checkDate)) {
            currentStreak++;
            streakStartDate = checkDate;
            checkDate = checkDate.minusDays(1);
        }

        // Calculate longest streak
        int longestStreak = 0;
        int tempStreak = 0;
        LocalDate prevDate = null;

        // Sort dates in ascending order for longest streak calculation
        List<LocalDate> sortedDates = recordDates.stream()
                .sorted()
                .collect(Collectors.toList());

        for (LocalDate date : sortedDates) {
            if (prevDate == null || date.equals(prevDate.plusDays(1))) {
                tempStreak++;
                longestStreak = Math.max(longestStreak, tempStreak);
            } else {
                tempStreak = 1;
            }
            prevDate = date;
        }

        return StreakDto.of(
                recordTypeId,
                recordType.getName(),
                currentStreak,
                longestStreak,
                streakStartDate,
                lastLoggedDate,
                isActive
        );
    }

    /**
     * Calculate current and longest streak for a group
     * A group has a streak when the completion rule is met for consecutive days
     */
    public GroupStreakDto calculateGroupStreak(String groupId, String userEmail) {
        // Verify user is a member
        if (!membershipRepository.isUserActiveMemberOfGroup(groupId, userEmail)) {
            throw new SecurityException("Not a member of this group");
        }

        UserGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        RecordType recordType = group.getRecordType();

        // Get all unique dates where records exist for this group's record type
        List<com.example.demo.model.Record> allRecords = recordRepository
                .findByRecordTypeRecordTypeIdOrderByRecordDateDesc(recordType.getRecordTypeId());

        if (allRecords.isEmpty()) {
            return GroupStreakDto.of(
                    groupId,
                    group.getName(),
                    recordType.getRecordTypeId(),
                    recordType.getName(),
                    0,
                    0,
                    null,
                    null,
                    false
            );
        }

        // Get unique dates
        Set<LocalDate> uniqueDates = allRecords.stream()
                .map(com.example.demo.model.Record::getRecordDate)
                .collect(Collectors.toSet());

        // For each date, check if the group completion rule was met
        Set<LocalDate> completedDates = uniqueDates.stream()
                .filter(date -> {
                    try {
                        var progress = progressService.getDailyProgress(groupId, date, userEmail);
                        return progress.completionMet();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toSet());

        if (completedDates.isEmpty()) {
            return GroupStreakDto.of(
                    groupId,
                    group.getName(),
                    recordType.getRecordTypeId(),
                    recordType.getName(),
                    0,
                    0,
                    null,
                    null,
                    false
            );
        }

        // Calculate current streak
        int currentStreak = 0;
        LocalDate today = LocalDate.now();
        LocalDate checkDate = today;
        LocalDate streakStartDate = null;

        // Check if streak is still active (today or yesterday completed)
        boolean isActive = completedDates.contains(today) || completedDates.contains(today.minusDays(1));

        // Start checking from today or yesterday
        if (!completedDates.contains(today)) {
            checkDate = today.minusDays(1);
        }

        while (completedDates.contains(checkDate)) {
            currentStreak++;
            streakStartDate = checkDate;
            checkDate = checkDate.minusDays(1);
        }

        // Calculate longest streak
        int longestStreak = 0;
        int tempStreak = 0;
        LocalDate prevDate = null;

        List<LocalDate> sortedCompletedDates = completedDates.stream()
                .sorted()
                .collect(Collectors.toList());

        for (LocalDate date : sortedCompletedDates) {
            if (prevDate == null || date.equals(prevDate.plusDays(1))) {
                tempStreak++;
                longestStreak = Math.max(longestStreak, tempStreak);
            } else {
                tempStreak = 1;
            }
            prevDate = date;
        }

        LocalDate lastCompletedDate = sortedCompletedDates.isEmpty() ? null :
                sortedCompletedDates.get(sortedCompletedDates.size() - 1);

        return GroupStreakDto.of(
                groupId,
                group.getName(),
                recordType.getRecordTypeId(),
                recordType.getName(),
                currentStreak,
                longestStreak,
                streakStartDate,
                lastCompletedDate,
                isActive
        );
    }
}

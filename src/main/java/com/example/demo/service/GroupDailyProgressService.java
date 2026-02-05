package com.example.demo.service;

import com.example.demo.dto.GroupDailyProgressDto;
import com.example.demo.dto.MemberProgressDto;
import com.example.demo.model.*;
import com.example.demo.model.Record;
import com.example.demo.repo.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupDailyProgressService {

    private final UserGroupRepository groupRepository;
    private final GroupMembershipRepository membershipRepository;
    private final RecordRepository recordRepository;

    public GroupDailyProgressService(UserGroupRepository groupRepository,
                                     GroupMembershipRepository membershipRepository,
                                     RecordRepository recordRepository) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.recordRepository = recordRepository;
    }

    public GroupDailyProgressDto getDailyProgress(String groupId, LocalDate date, String userEmail) {
        // Verify user is a member
        if (!membershipRepository.isUserActiveMemberOfGroup(groupId, userEmail)) {
            throw new SecurityException("Not a member of this group");
        }

        // Get the group
        UserGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Get all active members
        List<GroupMembership> memberships = membershipRepository.findActiveMembersOfGroup(groupId);

        // Get all records for this group's RecordType on this date
        List<Record> recordsForDate = recordRepository.findByRecordTypeAndDate(
                group.getRecordType().getRecordTypeId(),
                date
        );

        // Create sets for quick lookup
        Set<String> userIdsWhoLogged = recordsForDate.stream()
                .map(r -> r.getUser().getId())
                .collect(Collectors.toSet());

        // Build member status list
        List<MemberProgressDto> memberProgress = new ArrayList<>();

        for (GroupMembership membership : memberships) {
            User member = membership.getUser();
            boolean hasLogged = userIdsWhoLogged.contains(member.getId());

            // Find the specific record if they logged
            Record record = recordsForDate.stream()
                    .filter(r -> r.getUser().getId().equals(member.getId()))
                    .findFirst()
                    .orElse(null);

            memberProgress.add(new MemberProgressDto(
                    member.getId(),
                    member.getEmail(),
                    member.getUsername(),
                    hasLogged,
                    record != null ? record.getRawData() : null,
                    membership.getRole()
            ));
        }

        // Calculate completion status
        int totalMembers = memberships.size();
        int membersWhoLogged = userIdsWhoLogged.size();
        boolean completionMet = evaluateCompletionRule(
                membersWhoLogged,
                totalMembers,
                group.getCompletionRule(),
                group.getCustomPercentage()
        );

        return new GroupDailyProgressDto(
                groupId,
                group.getName(),
                date,
                totalMembers,
                membersWhoLogged,
                completionMet,
                memberProgress
        );
    }

    private boolean evaluateCompletionRule(int logged, int total, CompletionRule rule, Integer customPercentage) {
        return switch (rule) {
            case ALL_MEMBERS -> logged == total;
            case MAJORITY -> logged > (total / 2.0);
            case CUSTOM_PERCENTAGE -> logged >= (total * customPercentage / 100.0);
        };
    }
}
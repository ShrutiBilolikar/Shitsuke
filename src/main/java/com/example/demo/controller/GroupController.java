package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.GroupMembership;
import com.example.demo.model.UserGroup;
import com.example.demo.repo.GroupMembershipRepository;
import com.example.demo.service.GroupDailyProgressService;
import com.example.demo.service.UserGroupService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final UserGroupService userGroupService;
    private final GroupDailyProgressService progressService;
    private final GroupMembershipRepository membershipRepository;

    public GroupController(UserGroupService userGroupService,
                          GroupDailyProgressService progressService,
                          GroupMembershipRepository membershipRepository) {
        this.userGroupService = userGroupService;
        this.progressService = progressService;
        this.membershipRepository = membershipRepository;
    }

    /**
     * Create a new group
     * POST /api/groups
     */
    @PostMapping
    public ResponseEntity<UserGroupDto> createGroup(
            @RequestBody UserGroupCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserGroup group = userGroupService.createGroup(userDetails.getUsername(), request);

        // Get active member count (creator is auto-added as first member)
        Long memberCount = membershipRepository.countActiveMembersOfGroup(group.getGroupId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserGroupDto.fromEntity(group, memberCount));
    }

    /**
     * Get all groups for current user
     * GET /api/groups
     */
    @GetMapping
    public ResponseEntity<List<UserGroupDto>> getUserGroups(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<UserGroup> groups = userGroupService.getUserGroups(userDetails.getUsername());

        List<UserGroupDto> groupDtos = groups.stream()
                .map(group -> {
                    Long memberCount = membershipRepository.countActiveMembersOfGroup(group.getGroupId());
                    return UserGroupDto.fromEntity(group, memberCount);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(groupDtos);
    }

    /**
     * Get specific group details
     * GET /api/groups/{groupId}
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<UserGroupDto> getGroupDetails(
            @PathVariable String groupId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserGroup group = userGroupService.getGroupDetails(groupId, userDetails.getUsername());
        Long memberCount = membershipRepository.countActiveMembersOfGroup(groupId);

        return ResponseEntity.ok(UserGroupDto.fromEntity(group, memberCount));
    }

    /**
     * Get all members of a group
     * GET /api/groups/{groupId}/members
     */
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMembershipDto>> getGroupMembers(
            @PathVariable String groupId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<GroupMembership> members = userGroupService.getGroupMembers(groupId, userDetails.getUsername());

        List<GroupMembershipDto> memberDtos = members.stream()
                .map(GroupMembershipDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(memberDtos);
    }

    /**
     * Invite users to a group (friends only)
     * POST /api/groups/{groupId}/invite
     */
    @PostMapping("/{groupId}/invite")
    public ResponseEntity<List<GroupMembershipDto>> inviteUsersToGroup(
            @PathVariable String groupId,
            @RequestBody GroupInviteRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<GroupMembership> invitations = userGroupService.inviteUsersToGroup(
                groupId,
                request.userEmails(),
                userDetails.getUsername()
        );

        List<GroupMembershipDto> invitationDtos = invitations.stream()
                .map(GroupMembershipDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(invitationDtos);
    }

    /**
     * Get pending group invitations for current user
     * GET /api/groups/invitations/pending
     */
    @GetMapping("/invitations/pending")
    public ResponseEntity<List<GroupMembershipDto>> getPendingInvitations(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<GroupMembership> invitations = userGroupService.getPendingInvitations(userDetails.getUsername());

        List<GroupMembershipDto> invitationDtos = invitations.stream()
                .map(GroupMembershipDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(invitationDtos);
    }

    /**
     * Accept a group invitation
     * POST /api/groups/invitations/{membershipId}/accept
     */
    @PostMapping("/invitations/{membershipId}/accept")
    public ResponseEntity<GroupMembershipDto> acceptInvitation(
            @PathVariable String membershipId,
            @AuthenticationPrincipal UserDetails userDetails) {
        GroupMembership membership = userGroupService.acceptInvitation(
                membershipId,
                userDetails.getUsername()
        );

        return ResponseEntity.ok(GroupMembershipDto.fromEntity(membership));
    }

    /**
     * Reject a group invitation
     * POST /api/groups/invitations/{membershipId}/reject
     */
    @PostMapping("/invitations/{membershipId}/reject")
    public ResponseEntity<Void> rejectInvitation(
            @PathVariable String membershipId,
            @AuthenticationPrincipal UserDetails userDetails) {
        userGroupService.rejectInvitation(membershipId, userDetails.getUsername());

        return ResponseEntity.ok().build();
    }

    /**
     * Leave a group (creators cannot leave, they must archive)
     * POST /api/groups/{groupId}/leave
     */
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(
            @PathVariable String groupId,
            @AuthenticationPrincipal UserDetails userDetails) {
        userGroupService.leaveGroup(groupId, userDetails.getUsername());

        return ResponseEntity.ok().build();
    }

    /**
     * Archive a group (creator only)
     * POST /api/groups/{groupId}/archive
     */
    @PostMapping("/{groupId}/archive")
    public ResponseEntity<Void> archiveGroup(
            @PathVariable String groupId,
            @AuthenticationPrincipal UserDetails userDetails) {
        userGroupService.archiveGroup(groupId, userDetails.getUsername());

        return ResponseEntity.ok().build();
    }

    /**
     * Get daily progress for a group on a specific date
     * GET /api/groups/{groupId}/progress?date=2026-01-15
     */
    @GetMapping("/{groupId}/progress")
    public ResponseEntity<GroupDailyProgressDto> getDailyProgress(
            @PathVariable String groupId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {
        GroupDailyProgressDto progress = progressService.getDailyProgress(
                groupId,
                date,
                userDetails.getUsername()
        );

        return ResponseEntity.ok(progress);
    }

    /**
     * Get daily progress for today (convenience endpoint)
     * GET /api/groups/{groupId}/progress/today
     */
    @GetMapping("/{groupId}/progress/today")
    public ResponseEntity<GroupDailyProgressDto> getTodayProgress(
            @PathVariable String groupId,
            @AuthenticationPrincipal UserDetails userDetails) {
        GroupDailyProgressDto progress = progressService.getDailyProgress(
                groupId,
                LocalDate.now(),
                userDetails.getUsername()
        );

        return ResponseEntity.ok(progress);
    }
}

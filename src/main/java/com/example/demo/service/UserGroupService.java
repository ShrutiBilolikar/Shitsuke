package com.example.demo.service;
import com.example.demo.dto.UserGroupCreateRequest;
import com.example.demo.model.*;
import com.example.demo.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserGroupService {

    private final UserGroupRepository groupRepository;
    private final GroupMembershipRepository membershipRepository;
    private final RecordTypeRepository recordTypeRepository;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public UserGroupService(UserGroupRepository groupRepository,
                            GroupMembershipRepository membershipRepository,
                            RecordTypeRepository recordTypeRepository,
                            UserRepository userRepository,
                            FriendshipRepository friendshipRepository) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.recordTypeRepository = recordTypeRepository;
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    @Transactional
    public UserGroup createGroup(String creatorEmail, UserGroupCreateRequest request){
        User creator = userRepository.findByEmail(creatorEmail).orElseThrow(()->new RuntimeException("User not found"));
        RecordType recordType = recordTypeRepository.findById(request.getRecordTypeId()).orElseThrow(()->new RuntimeException("RecordType not found"));
        if(!recordType.getUser().getEmail().equals(creatorEmail)){
            throw new SecurityException("You can only create groups for your own record types");
        }
        if(request.getCompletionRule()== CompletionRule.CUSTOM_PERCENTAGE){
            if(request.getCustomPercentage() == null || request.getCustomPercentage()<1||request.getCustomPercentage()>100){
                throw new IllegalArgumentException("Custom percent must be between 1 to 100");
            }
        }
        UserGroup group = new UserGroup();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setCreator(creator);
        group.setRecordType(recordType);
        group.setCompletionRule(request.getCompletionRule());
        group.setCustomPercentage(request.getCustomPercentage());
        group.setStatus(GroupStatus.ACTIVE);

        UserGroup savedGroup = groupRepository.save(group);

        // Auto-add creator as ACTIVE member with CREATOR role
        GroupMembership creatorMembership = new GroupMembership();
        creatorMembership.setGroup(savedGroup);
        creatorMembership.setUser(creator);
        creatorMembership.setStatus(MembershipStatus.ACTIVE);
        creatorMembership.setRole(MembershipRole.CREATOR);
        creatorMembership.setJoinedAt(Instant.now());
        membershipRepository.save(creatorMembership);

        // Mark RecordType as group metric
        recordType.setIsGroupMetric(true);
        recordTypeRepository.save(recordType);
        return savedGroup;
    }

    @Transactional
    public List<GroupMembership> inviteUsersToGroup(String groupId, List<String> userEmails, String inviterEmail) {
        UserGroup group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));

        // Verify inviter is an active member
        if (!membershipRepository.isUserActiveMemberOfGroup(groupId, inviterEmail)) {
            throw new SecurityException("You must be a member to invite others");
        }

        List<GroupMembership> invitations = new ArrayList<>();

        for (String userEmail : userEmails) {
            // Check if users are friends
            if (!friendshipRepository.areUsersFriends(inviterEmail, userEmail)) {
                throw new SecurityException("You can only invite friends to groups: " + userEmail);
            }

            // Check if user already invited or member
            if (membershipRepository.existsActiveOrInvitedMembership(groupId, userEmail)) {
                continue; // Skip already invited/active members
            }

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

            GroupMembership membership = new GroupMembership();
            membership.setGroup(group);
            membership.setUser(user);
            membership.setStatus(MembershipStatus.INVITED);
            membership.setRole(MembershipRole.MEMBER);

            invitations.add(membershipRepository.save(membership));
        }

        return invitations;
    }
    @Transactional
    public GroupMembership acceptInvitation(String membershipId, String userEmail) {
        GroupMembership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        // Verify the current user is the invited user
        if (!membership.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("You can only accept your own invitations");
        }

        if (membership.getStatus() != MembershipStatus.INVITED) {
            throw new IllegalStateException("Invitation is not pending");
        }

        membership.setStatus(MembershipStatus.ACTIVE);
        membership.setJoinedAt(Instant.now());

        return membershipRepository.save(membership);
    }

    @Transactional
    public void rejectInvitation(String membershipId, String userEmail) {
        GroupMembership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        if (!membership.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("You can only reject your own invitations");
        }

        if (membership.getStatus() != MembershipStatus.INVITED) {
            throw new IllegalStateException("Invitation is not pending");
        }

        membershipRepository.delete(membership);
    }

    @Transactional
    public void leaveGroup(String groupId, String userEmail) {
        GroupMembership membership = membershipRepository.findByGroupGroupIdAndUserEmail(groupId, userEmail)
                .orElseThrow(() -> new RuntimeException("Membership not found"));

        if (membership.getRole() == MembershipRole.CREATOR) {
            throw new IllegalStateException("Group creator cannot leave. Archive the group instead.");
        }

        if (membership.getStatus() != MembershipStatus.ACTIVE) {
            throw new IllegalStateException("You are not an active member of this group");
        }

        membership.setStatus(MembershipStatus.LEFT);
        membership.setLeftAt(Instant.now());
        membershipRepository.save(membership);
    }

    public List<UserGroup> getUserGroups(String userEmail) {
        List<GroupMembership> memberships = membershipRepository.findActiveMembershipsForUser(userEmail);
        return memberships.stream()
                .map(GroupMembership::getGroup)
                .toList();
    }

    public UserGroup getGroupDetails(String groupId, String userEmail) {
        if (!membershipRepository.isUserActiveMemberOfGroup(groupId, userEmail)) {
            throw new SecurityException("You must be a member to view group details");
        }

        return groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }

    public List<GroupMembership> getGroupMembers(String groupId, String userEmail) {
        if (!membershipRepository.isUserActiveMemberOfGroup(groupId, userEmail)) {
            throw new SecurityException("You must be a member to view members");
        }

        return membershipRepository.findActiveMembersOfGroup(groupId);
    }

    public List<GroupMembership> getPendingInvitations(String userEmail) {
        return membershipRepository.findPendingInvitationsForUser(userEmail);
    }

    @Transactional
    public void archiveGroup(String groupId, String userEmail) {
        UserGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getCreator().getEmail().equals(userEmail)) {
            throw new SecurityException("Only the group creator can archive the group");
        }

        group.setStatus(GroupStatus.ARCHIVED);
        groupRepository.save(group);
    }

}

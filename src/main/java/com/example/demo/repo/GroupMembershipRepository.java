package com.example.demo.repo;

import com.example.demo.model.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership,String> {
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.user.email = :email AND gm.status = 'ACTIVE'")
    List<GroupMembership> findActiveMembershipsForUser(@Param("email") String email);

    // Find pending invitations for a user
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.user.email = :email AND gm.status = 'INVITED'")
    List<GroupMembership> findPendingInvitationsForUser(@Param("email") String email);

    // Find all active members of a group
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.group.groupId = :groupId AND gm.status = 'ACTIVE'")
    List<GroupMembership> findActiveMembersOfGroup(@Param("groupId") String groupId);

    // Count active members
    @Query("SELECT COUNT(gm) FROM GroupMembership gm WHERE gm.group.groupId = :groupId AND gm.status = 'ACTIVE'")
    Long countActiveMembersOfGroup(@Param("groupId") String groupId);

    // Check if user is active member of group
    @Query("SELECT CASE WHEN COUNT(gm) > 0 THEN true ELSE false END FROM GroupMembership gm " +
            "WHERE gm.group.groupId = :groupId AND gm.user.email = :email AND gm.status = 'ACTIVE'")
    boolean isUserActiveMemberOfGroup(@Param("groupId") String groupId, @Param("email") String email);

    // Find membership by group and user
    Optional<GroupMembership> findByGroupGroupIdAndUserEmail(String groupId, String email);

    // Check if invitation already exists
    @Query("SELECT CASE WHEN COUNT(gm) > 0 THEN true ELSE false END FROM GroupMembership gm " +
            "WHERE gm.group.groupId = :groupId AND gm.user.email = :email AND gm.status IN ('INVITED', 'ACTIVE')")
    boolean existsActiveOrInvitedMembership(@Param("groupId") String groupId, @Param("email") String email);

    // Check if user is member of any group using a specific RecordType
    @Query("""
        SELECT CASE WHEN COUNT(gm) > 0 THEN true ELSE false END
        FROM GroupMembership gm
        WHERE gm.user.email = :userEmail
        AND gm.group.recordType.recordTypeId = :recordTypeId
        AND gm.status = 'ACTIVE'
    """)
    boolean isUserMemberOfAnyGroupWithRecordType(
            @Param("userEmail") String userEmail,
            @Param("recordTypeId") String recordTypeId
    );
}

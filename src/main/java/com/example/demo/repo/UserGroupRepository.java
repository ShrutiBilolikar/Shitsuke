package com.example.demo.repo;

import com.example.demo.model.UserGroup;
import com.example.demo.model.GroupStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface UserGroupRepository  extends JpaRepository <UserGroup, String>{
    // Find all groups created by a user
    @Query("SELECT g FROM UserGroup g WHERE g.creator.email = :email AND g.status = 'ACTIVE'")
    List<UserGroup> findGroupsCreatedByUser(@Param("email") String email);

    // Find all active groups
    @Query("SELECT g FROM UserGroup g WHERE g.status = 'ACTIVE'")
    List<UserGroup> findAllActiveGroups();

    // Find groups using a specific RecordType
    @Query("SELECT g FROM UserGroup g WHERE g.recordType.recordTypeId = :recordTypeId AND g.status = 'ACTIVE'")
    List<UserGroup> findActiveGroupsByRecordType(@Param("recordTypeId") String recordTypeId);

    // Check if user is member of any group using this RecordType
    @Query("""
        SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END
        FROM UserGroup g
        JOIN GroupMembership gm ON gm.group.groupId = g.groupId
        WHERE g.recordType.recordTypeId = :recordTypeId
        AND gm.user.email = :userEmail
        AND gm.status = 'ACTIVE'
        AND g.status = 'ACTIVE'
    """)
    boolean existsByRecordTypeAndMemberEmail(
            @Param("recordTypeId") String recordTypeId,
            @Param("userEmail") String userEmail
    );
}

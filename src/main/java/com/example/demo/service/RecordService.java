package com.example.demo.service;

import com.example.demo.dto.RecordCreateRequest;
import com.example.demo.dto.RecordRequest;
import com.example.demo.model.Record;
import com.example.demo.model.RecordType;
import com.example.demo.model.User;
import com.example.demo.model.UserGroup;
import com.example.demo.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Service
public class RecordService {

    private final RecordRepository recordRepository;
    private final RecordTypeRepository recordTypeRepository;
    private final UserRepository userRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final UserGroupRepository groupRepository;
    public RecordService(RecordRepository recordRepository,
                         RecordTypeRepository recordTypeRepository,
                         UserRepository userRepository,
                         GroupMembershipRepository groupMembershipRepository,
                         UserGroupRepository groupRepository) {
        this.recordRepository = recordRepository;
        this.recordTypeRepository = recordTypeRepository;
        this.userRepository = userRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.groupRepository=groupRepository;
    }
    @Transactional
    public Record createRecord(RecordCreateRequest request, String recordTypeId, String userEmail) {
        RecordType recordType = recordTypeRepository.findById(recordTypeId)
                .orElseThrow(() -> new RuntimeException("RecordType not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // CRITICAL: Authorization check
        boolean canAccess = false;

        if (recordType.getIsGroupMetric()) {
            // For group RecordTypes: check if user is member of any group using this RecordType
            canAccess = groupMembershipRepository
                    .isUserMemberOfAnyGroupWithRecordType(userEmail, recordType.getRecordTypeId());
        } else {
            // For personal RecordTypes: check if user is the owner
            canAccess = recordType.getUser().getEmail().equals(userEmail);
        }

        if (!canAccess) {
            throw new SecurityException("You don't have access to this RecordType");
        }

        // Check for duplicate record (same user, recordType, and date)
        Optional<Record> existing = recordRepository.findByUserAndRecordTypeAndDate(
                user.getId(),
                recordType.getRecordTypeId(),
                request.getRecordDate()
        );

        if (existing.isPresent()) {
            throw new IllegalStateException("You already logged a record for this date");
        }

        // Create record with BOTH user and recordType
        Record record = new Record();
        record.setUser(user);  // WHO is logging
        record.setRecordType(recordType);  // WHAT they're tracking
        record.setRecordDate(request.getRecordDate());
        record.setType(recordType.getType());
        record.setRawData(request.getRawData());

        return recordRepository.save(record);
    }
    @Transactional
    public Record createRecordForGroup(String groupId, RecordCreateRequest request, String userEmail) {
        // Verify user is an active member of the group
        if (!groupMembershipRepository.isUserActiveMemberOfGroup(groupId, userEmail)) {
            throw new SecurityException("You are not a member of this group");
        }

        // Get the group to find its RecordType
        UserGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Create record using the group's RecordType
        return createRecord(request, group.getRecordType().getRecordTypeId(), userEmail);
    }

    public List<Record> getRecordsByUserAndType(String userEmail, String recordTypeId) {
        return recordRepository.findByUserAndRecordType(userEmail, recordTypeId);
    }

    public Double getMonthlyTotal(String recordTypeId, int month, int year) {
        RecordType recordType = recordTypeRepository.findById(recordTypeId)
                .orElseThrow(() -> new RuntimeException("RecordType not found"));
        return recordRepository.getTotalForMonth(recordType, month, year);
    }

    public Long getBooleanCountForMonth(String recordTypeId, int month, int year) {
        RecordType recordType = recordTypeRepository.findById(recordTypeId)
                .orElseThrow(() -> new RuntimeException("RecordType not found"));
        return recordRepository.getCountForMonth(recordType, month, year);
    }

    public RecordType getRecordType(String recordTypeId) {
        RecordType recordType = recordTypeRepository.findById(recordTypeId).orElseThrow(() -> new RuntimeException("RecordType not found"));
        return recordType;
    }

    public RecordType getRecordTypeForUser(String recordTypeId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return recordTypeRepository.findById(recordTypeId)
                .filter(rt -> rt.getUser().equals(user))
                .orElseThrow(() -> new RuntimeException("RecordType not found for this user"));

    }

    public List<Record>getRecordsForType(String recordTypeId,String email){
        System.out.println("RECORDTYPEID and EMAIL : " + recordTypeId+ " " + email);
        return recordRepository.findRecordsByTypeAndUser(recordTypeId,email);
    }
}

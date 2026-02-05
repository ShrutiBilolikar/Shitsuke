package com.example.demo.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_groups")
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false,updatable = false)
    private String groupId;

    @Column(nullable = false,length = 100)
    private String name;
    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false, referencedColumnName = "id")
    private User creator;

    // CRITICAL: Links to the shared RecordType that all members track
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_type_id", nullable = false, referencedColumnName = "recordTypeId")
    private RecordType recordType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CompletionRule completionRule;

    @Column
    private Integer customPercentage;  // Used when completionRule = CUSTOM_PERCENTAGE (1-100)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupStatus status = GroupStatus.ACTIVE;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    // Constructors
    public UserGroup() {}

    // Getters and Setters
    public String getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    public CompletionRule getCompletionRule() {
        return completionRule;
    }

    public void setCompletionRule(CompletionRule completionRule) {
        this.completionRule = completionRule;
    }

    public Integer getCustomPercentage() {
        return customPercentage;
    }

    public void setCustomPercentage(Integer customPercentage) {
        this.customPercentage = customPercentage;
    }

    public GroupStatus getStatus() {
        return status;
    }

    public void setStatus(GroupStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}


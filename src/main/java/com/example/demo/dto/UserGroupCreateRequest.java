package com.example.demo.dto;

import com.example.demo.model.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

// Request to create a group
public class UserGroupCreateRequest {
    private String name;
    private String description;
    private String recordTypeId;
    private CompletionRule completionRule;
    private Integer customPercentage;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRecordTypeId() { return recordTypeId; }
    public void setRecordTypeId(String recordTypeId) { this.recordTypeId = recordTypeId; }

    public CompletionRule getCompletionRule() { return completionRule; }
    public void setCompletionRule(CompletionRule completionRule) { this.completionRule = completionRule; }

    public Integer getCustomPercentage() { return customPercentage; }
    public void setCustomPercentage(Integer customPercentage) { this.customPercentage = customPercentage; }
}

package com.example.demo.dto;

import java.time.LocalDate;
import com.example.demo.model.Record;

public record RecordDto(
        String recordId,
        LocalDate recordDate,
        String type,
        String rawData,
        String recordTypeId,
        String recordTypeName,
        String userId,
        String userEmail,
        String username
) {
    public static RecordDto fromEntity(Record record) {
        return new RecordDto(
                record.getRecordId(),
                record.getRecordDate(),
                record.getType().name(),
                record.getRawData(),
                record.getRecordType().getRecordTypeId(),
                record.getRecordType().getName(),
                record.getUser().getId(),
                record.getUser().getEmail(),
                record.getUser().getUsername()
        );
    }
}
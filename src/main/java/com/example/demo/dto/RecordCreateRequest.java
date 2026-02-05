package com.example.demo.dto;

import java.time.LocalDate;

public class RecordCreateRequest {
    private LocalDate recordDate;
    private String rawData;

    // Getters and setters
    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    public String getRawData() { return rawData; }
    public void setRawData(String rawData) { this.rawData = rawData; }
}

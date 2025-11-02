package com.example.demo.dto;


import java.time.LocalDate;

public class RecordRequest {
    private String recordTypeId;
    private LocalDate recordDate;
    private String rawData;

    public String getRecordTypeId() {
        return this.recordTypeId;
    }
    public LocalDate getRecordDate(){
        return this.recordDate;
    }
    public String getRawData(){
        return this.rawData;
    }
    public void setRecordTypeId(String recordTypeId) {
        this.recordTypeId = recordTypeId;
    }
    public void setRecordDate(LocalDate recordDate){
        this.recordDate=recordDate;
    }
    public void setRawData(String rawData){
        this.rawData=rawData;
    }
}

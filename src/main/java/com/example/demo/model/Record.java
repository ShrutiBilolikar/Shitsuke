package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "records")

public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false,updatable = false,unique = true)
    private String recordId;

    @Column(nullable = false, updatable = false)
    private LocalDate recordDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column
    private String rawData;

    @ManyToOne
    @JoinColumn(name = "record_type_id", referencedColumnName = "recordTypeId")
    private RecordType recordType;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType=recordType;
    }
    public void setType(Type type){
        this.type = type;
    }
    public String getRecordId() {
        return recordId;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public Type getType() {
        return type;
    }

    public String getRawData() {
        return rawData;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

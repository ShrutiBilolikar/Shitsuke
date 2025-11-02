package com.example.demo.service;

import com.example.demo.dto.RecordRequest;
import com.example.demo.model.Record;
import com.example.demo.model.RecordType;
import com.example.demo.model.User;
import com.example.demo.repo.RecordRepository;
import com.example.demo.repo.RecordTypeRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RecordService {

    private final RecordRepository recordRepository;
    private final RecordTypeRepository recordTypeRepository;
    private final UserRepository userRepository;
    public RecordService(RecordRepository recordRepository, RecordTypeRepository recordTypeRepository, UserRepository userRepository) {
        this.recordRepository = recordRepository;
        this.recordTypeRepository = recordTypeRepository;
        this.userRepository = userRepository;
    }
    public Record createRecord(RecordRequest request, String userEmail) {
        System.out.println("CREATERECORD"+request+" "+userEmail);
        User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new RuntimeException("User not found"));
        RecordType recordType = recordTypeRepository.findById(request.getRecordTypeId())
                .filter(rt -> rt.getUser().equals(user))
                .orElseThrow(() -> new RuntimeException("RecordType not found for this user"));

        boolean exists = recordRepository.existsByRecordTypeAndRecordDate(
                recordType,
                request.getRecordDate()
        );

        if (exists) {
            throw new RuntimeException("Record already exists for this date.");
        }

        Record record = new Record();
        record.setRecordDate(request.getRecordDate());
        record.setRawData(request.getRawData());
        record.setRecordType(recordType);
        record.setType(recordType.getType()); // auto set type

        return recordRepository.save(record);
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
}

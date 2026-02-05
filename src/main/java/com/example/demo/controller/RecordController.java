package com.example.demo.controller;

import com.example.demo.dto.RecordRequest;
import com.example.demo.dto.RecordCreateRequest;
import com.example.demo.model.Record;
import com.example.demo.model.RecordType;
import com.example.demo.model.Type;
import com.example.demo.service.RecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    public ResponseEntity<Record> createRecord(@RequestBody RecordRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        // Convert RecordRequest to RecordCreateRequest
        RecordCreateRequest createRequest = new RecordCreateRequest();
        createRequest.setRecordDate(request.getRecordDate());
        createRequest.setRawData(request.getRawData());

        Record created = recordService.createRecord(createRequest, request.getRecordTypeId(), userDetails.getUsername());
        return ResponseEntity.ok(created);
    }
    @GetMapping("/summary/{recordTypeId}")
    public ResponseEntity<?>getSummary(@PathVariable String recordTypeId,@RequestParam int month, @RequestParam int year, @AuthenticationPrincipal UserDetails userDetails){
        System.out.println("[RecordController#getSummary] recordTypeId=" + recordTypeId + ", month=" + month + ", year=" + year + ", user=" + (userDetails == null ? "null" : userDetails.getUsername()));
        String userEmail = userDetails.getUsername();
        RecordType rt = recordService.getRecordTypeForUser(recordTypeId, userEmail);
        if(rt.getType() == Type.Number){
            return ResponseEntity.ok(recordService.getMonthlyTotal(recordTypeId,month,year));
        }
        else if (rt.getType() == Type.Boolean) {
            return ResponseEntity.ok(recordService.getBooleanCountForMonth(recordTypeId, month, year));
        } else {
            return ResponseEntity.ok("No numeric summary for this type");
        }
    }
    @GetMapping("/record-type/{id}")
    public ResponseEntity<List<Record>> getRecordsByRecordType(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<Record> records = recordService.getRecordsForType(id, userDetails.getUsername());
        return ResponseEntity.ok(records);
    }
}

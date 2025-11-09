package com.example.demo.controller;

import com.example.demo.dto.RecordTypeRequest;
import com.example.demo.model.RecordType;
import com.example.demo.service.RecordTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/record-types")
public class RecordTypeController {

    private final RecordTypeService recordTypeService;

    public RecordTypeController(RecordTypeService recordTypeService) {
        this.recordTypeService = recordTypeService;
    }

    @PostMapping
    public ResponseEntity<RecordType> createRecordType(
            @RequestBody RecordTypeRequest request,
            @AuthenticationPrincipal UserDetails userDetails // JWT-authenticated user
    ) {
        RecordType created = recordTypeService.createRecordType(request, userDetails.getUsername());
        return ResponseEntity.ok(created);
    }
    @GetMapping("/all-records")
    public ResponseEntity<List<RecordType>> getRecordTypes(@AuthenticationPrincipal UserDetails userDetails) {
        List<RecordType> list = recordTypeService.getRecordTypes(userDetails.getUsername());
        return ResponseEntity.ok(list);
    }
}

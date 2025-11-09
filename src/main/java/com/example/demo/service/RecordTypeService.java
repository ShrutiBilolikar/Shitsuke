package com.example.demo.service;

import com.example.demo.dto.RecordTypeRequest;
import com.example.demo.model.RecordType;
import com.example.demo.model.Type;
import com.example.demo.model.User;
import com.example.demo.repo.RecordTypeRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordTypeService {

    private final RecordTypeRepository recordTypeRepository;
    private final UserRepository userRepository;

    public RecordTypeService(RecordTypeRepository recordTypeRepository, UserRepository userRepository) {
        this.recordTypeRepository = recordTypeRepository;
        this.userRepository = userRepository;
    }

    public RecordType createRecordType(RecordTypeRequest request, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RecordType rt = new RecordType();
        rt.setName(request.getName());
        rt.setType(Type.valueOf(request.getType()));
        rt.setUser(user);

        return recordTypeRepository.save(rt);
    }

    public List<RecordType> getRecordTypes(String email) {
        return recordTypeRepository.findByUserEmail(email);
    }
}

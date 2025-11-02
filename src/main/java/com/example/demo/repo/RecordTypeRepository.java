package com.example.demo.repo;

import com.example.demo.model.RecordType;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordTypeRepository extends JpaRepository<RecordType,String> {
    Optional<RecordType> findByName(String name);
    boolean existsByName(String name);
}

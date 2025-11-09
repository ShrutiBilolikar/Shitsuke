package com.example.demo.repo;

import com.example.demo.model.RecordType;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecordTypeRepository extends JpaRepository<RecordType,String> {
    Optional<RecordType> findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT r FROM RecordType r WHERE r.user.email = :email")
    List<RecordType> findByUserEmail(@Param("email") String email);
}

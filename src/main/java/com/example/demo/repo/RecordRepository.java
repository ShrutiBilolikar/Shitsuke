package com.example.demo.repo;

import com.example.demo.model.Record;
import com.example.demo.model.RecordType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record,String > {
    Optional<Record>findByRecordTypeAndRecordDate(RecordType recordType, LocalDate recordDate);
    boolean existsByRecordTypeAndRecordDate(RecordType recordType, LocalDate recordDate);
    List<Record> findAllByRecordType(RecordType recordType);

    @Query("SELECT SUM(CAST(r.rawData AS double)) FROM Record r WHERE r.recordType = :recordType AND MONTH(r.recordDate) = :month AND YEAR(r.recordDate) = :year")
    Double getTotalForMonth(@Param("recordType") RecordType recordType, @Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(r) FROM Record r WHERE r.recordType = :recordType AND r.rawData = 'true' AND MONTH(r.recordDate) = :month AND YEAR(r.recordDate) = :year")
    Long getCountForMonth(@Param("recordType") RecordType recordType, @Param("month") int month, @Param("year") int year);

    @Query("SELECT r FROM Record r WHERE r.recordType.id = :recordTypeId AND r.recordType.user.email = :email")
    List<Record>findRecordsByTypeAndUser(@Param("recordTypeId")String recordTypeId, @Param("email")String email);
}

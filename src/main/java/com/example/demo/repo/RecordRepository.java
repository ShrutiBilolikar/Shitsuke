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

    @Query("SELECT r FROM Record r WHERE r.user.email = :email AND r.recordType.recordTypeId = :recordTypeId ORDER BY r.recordDate DESC")
    List<Record> findByUserAndRecordType(@Param("email") String email, @Param("recordTypeId") String recordTypeId);

    @Query("SELECT r FROM Record r WHERE r.user.id = :userId AND r.recordType.recordTypeId = :recordTypeId AND r.recordDate = :date")
    Optional<Record> findByUserAndRecordTypeAndDate(
            @Param("userId") String userId,
            @Param("recordTypeId") String recordTypeId,
            @Param("date") LocalDate date
    );
    @Query("SELECT SUM(CAST(r.rawData AS double)) FROM Record r WHERE r.recordType = :recordType AND MONTH(r.recordDate) = :month AND YEAR(r.recordDate) = :year")
    Double getTotalForMonth(@Param("recordType") RecordType recordType, @Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(r) FROM Record r WHERE r.recordType = :recordType AND r.rawData = 'true' AND MONTH(r.recordDate) = :month AND YEAR(r.recordDate) = :year")
    Long getCountForMonth(@Param("recordType") RecordType recordType, @Param("month") int month, @Param("year") int year);

    @Query("SELECT r FROM Record r WHERE r.recordType.id = :recordTypeId AND r.recordType.user.email = :email")
    List<Record>findRecordsByTypeAndUser(@Param("recordTypeId")String recordTypeId, @Param("email")String email);
    @Query("SELECT r FROM Record r WHERE r.recordType.recordTypeId = :recordTypeId AND r.recordDate = :date")
    List<Record> findByRecordTypeAndDate(
            @Param("recordTypeId") String recordTypeId,
            @Param("date") LocalDate date
    );

    // NEW: Count distinct users who logged for a RecordType on a date (for group streak calculation)
    @Query("SELECT COUNT(DISTINCT r.user.id) FROM Record r WHERE r.recordType.recordTypeId = :recordTypeId AND r.recordDate = :date")
    Long countDistinctUsersForDate(
            @Param("recordTypeId") String recordTypeId,
            @Param("date") LocalDate date
    );

    // NEW: Get all records for a user's RecordType within date range (for streak calculation)
    @Query("SELECT r FROM Record r WHERE r.user.id = :userId AND r.recordType.recordTypeId = :recordTypeId AND r.recordDate >= :startDate AND r.recordDate <= :endDate ORDER BY r.recordDate DESC")
    List<Record> findByUserAndRecordTypeAndDateRange(
            @Param("userId") String userId,
            @Param("recordTypeId") String recordTypeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // For user streak calculation
    @Query("SELECT r FROM Record r WHERE r.user.email = :email AND r.recordType.recordTypeId = :recordTypeId ORDER BY r.recordDate DESC")
    List<Record> findByUserEmailAndRecordTypeIdOrderByRecordDateDesc(
            @Param("email") String email,
            @Param("recordTypeId") String recordTypeId
    );

    // For group streak calculation - get all records for a recordType
    @Query("SELECT r FROM Record r WHERE r.recordType.recordTypeId = :recordTypeId ORDER BY r.recordDate DESC")
    List<Record> findByRecordTypeRecordTypeIdOrderByRecordDateDesc(
            @Param("recordTypeId") String recordTypeId
    );
}

package com.studyapp.repository;

import com.studyapp.domain.StudyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyLogRepository extends JpaRepository<StudyLog, Long> {
    
    List<StudyLog> findByUserId(Long userId);
    
    List<StudyLog> findByUserIdAndStudyDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT sl FROM StudyLog sl WHERE sl.user.id = :userId AND sl.studyDate = :studyDate")
    Optional<StudyLog> findByUserIdAndStudyDate(@Param("userId") Long userId, @Param("studyDate") LocalDate studyDate);
    
    @Query("SELECT SUM(sl.minutesStudied) FROM StudyLog sl WHERE sl.user.id = :userId AND sl.studyDate = :studyDate")
    Integer sumMinutesByUserIdAndStudyDate(@Param("userId") Long userId, @Param("studyDate") LocalDate studyDate);
    
    @Query("SELECT sl.studyDate, SUM(sl.minutesStudied) FROM StudyLog sl " +
           "WHERE sl.user.id = :userId AND sl.studyDate BETWEEN :startDate AND :endDate " +
           "GROUP BY sl.studyDate ORDER BY sl.studyDate")
    List<Object[]> getDailyStudyMinutes(@Param("userId") Long userId, 
                                       @Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate);
} 
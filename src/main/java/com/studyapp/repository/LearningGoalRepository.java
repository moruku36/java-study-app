package com.studyapp.repository;

import com.studyapp.domain.LearningGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningGoalRepository extends JpaRepository<LearningGoal, Long> {
    @Query("SELECT lg FROM LearningGoal lg WHERE lg.user.id = :userId AND (lg.isActive = true OR lg.isActive IS NULL)")
    List<LearningGoal> findActiveGoalsByUserId(@Param("userId") Long userId);

    List<LearningGoal> findByUserId(Long userId);
}

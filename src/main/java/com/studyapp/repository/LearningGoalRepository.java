package com.studyapp.repository;

import com.studyapp.domain.LearningGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningGoalRepository extends JpaRepository<LearningGoal, Long> {
    @Query("SELECT lg FROM LearningGoal lg WHERE lg.user.id = :userId AND (lg.isActive = true OR lg.isActive IS NULL) AND (lg.isDeleted = false OR lg.isDeleted IS NULL)")
    List<LearningGoal> findActiveGoalsByUserId(@Param("userId") Long userId);

    @Query("SELECT lg FROM LearningGoal lg WHERE lg.user.id = :userId AND (lg.isDeleted = false OR lg.isDeleted IS NULL)")
    List<LearningGoal> findByUserId(@Param("userId") Long userId);

    @Query("SELECT lg FROM LearningGoal lg WHERE lg.id = :id AND (lg.isDeleted = false OR lg.isDeleted IS NULL)")
    LearningGoal findNotDeletedById(@Param("id") Long id);
}

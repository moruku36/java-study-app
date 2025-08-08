package com.studyapp.service;

import com.studyapp.domain.LearningGoal;
import com.studyapp.repository.LearningGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Service
@Transactional
public class LearningGoalService {
    private final LearningGoalRepository learningGoalRepository;

    public LearningGoalService(LearningGoalRepository learningGoalRepository) {
        this.learningGoalRepository = learningGoalRepository;
    }

    public List<LearningGoal> findActiveGoalsByUserId(Long userId) {
        try { return learningGoalRepository.findActiveGoalsByUserId(userId); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    public List<LearningGoal> findByUserId(Long userId) {
        return learningGoalRepository.findByUserId(userId);
    }

    public LearningGoal save(LearningGoal goal) { return learningGoalRepository.save(goal); }

    public LearningGoal findNotDeletedById(Long id) {
        return learningGoalRepository.findNotDeletedById(id);
    }

    public void logicalDelete(Long id) {
        LearningGoal goal = learningGoalRepository.findById(id).orElse(null);
        if (goal != null) {
            goal.setIsDeleted(true);
            goal.setIsActive(false);
            goal.setDeletedAt(LocalDateTime.now());
            learningGoalRepository.save(goal);
        }
    }
}

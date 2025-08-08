package com.studyapp.service;

import com.studyapp.domain.LearningGoal;
import com.studyapp.repository.LearningGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
}

package com.studyapp.service;

import com.studyapp.domain.LearningGoal;
import com.studyapp.domain.User;
import com.studyapp.repository.LearningGoalRepository;
import com.studyapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
@Transactional
public class LearningGoalService {
    
    private final LearningGoalRepository learningGoalRepository;
    private final UserRepository userRepository;
    
    public LearningGoalService(LearningGoalRepository learningGoalRepository,
                              UserRepository userRepository) {
        this.learningGoalRepository = learningGoalRepository;
        this.userRepository = userRepository;
    }
    
    public List<LearningGoal> findAll() {
        return learningGoalRepository.findAll();
    }
    
    public Optional<LearningGoal> findById(Long id) {
        return learningGoalRepository.findById(id);
    }
    
    public List<LearningGoal> findByUserId(Long userId) {
        return learningGoalRepository.findByUserId(userId);
    }
    
    public List<LearningGoal> findActiveGoalsByUserId(Long userId) {
        try {
            return learningGoalRepository.findActiveGoalsByUserId(userId);
        } catch (Exception e) {
            System.err.println("アクティブな学習目標取得エラー: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public LearningGoal save(LearningGoal learningGoal) {
        return learningGoalRepository.save(learningGoal);
    }
    
    public LearningGoal createGoal(Long userId, String subject, Integer dailyTargetMinutes, 
                                  java.time.LocalDate startDate, java.time.LocalDate endDate) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("ユーザーが見つかりません: " + userId);
        }
        
        LearningGoal goal = new LearningGoal(userOpt.get(), subject, dailyTargetMinutes, startDate);
        if (endDate != null) {
            goal.setEndDate(endDate);
        }
        
        return learningGoalRepository.save(goal);
    }
    
    public LearningGoal updateGoal(Long goalId, String subject, Integer dailyTargetMinutes, 
                                  java.time.LocalDate startDate, java.time.LocalDate endDate) {
        Optional<LearningGoal> goalOpt = learningGoalRepository.findById(goalId);
        if (goalOpt.isEmpty()) {
            throw new IllegalArgumentException("学習目標が見つかりません: " + goalId);
        }
        
        LearningGoal goal = goalOpt.get();
        goal.setSubject(subject);
        goal.setDailyTargetMinutes(dailyTargetMinutes);
        goal.setStartDate(startDate);
        goal.setEndDate(endDate);
        
        return learningGoalRepository.save(goal);
    }
    
    public void deactivateGoal(Long goalId) {
        Optional<LearningGoal> goalOpt = learningGoalRepository.findById(goalId);
        if (goalOpt.isPresent()) {
            LearningGoal goal = goalOpt.get();
            goal.setIsActive(false);
            learningGoalRepository.save(goal);
        }
    }
    
    public void deleteById(Long id) {
        learningGoalRepository.deleteById(id);
    }
} 
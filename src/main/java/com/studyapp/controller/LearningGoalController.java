package com.studyapp.controller;

import com.studyapp.domain.LearningGoal;
import com.studyapp.service.LearningGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = "*")
public class LearningGoalController {
    
    @Autowired
    private LearningGoalService learningGoalService;
    
    @PostMapping
    public ResponseEntity<LearningGoal> createGoal(@RequestBody LearningGoal goal) {
        LearningGoal savedGoal = learningGoalService.save(goal);
        return ResponseEntity.ok(savedGoal);
    }
    
    @PostMapping("/create")
    public ResponseEntity<LearningGoal> createGoalWithParams(
            @RequestParam Long userId,
            @RequestParam String subject,
            @RequestParam Integer dailyTargetMinutes,
            @RequestParam String startDate,
            @RequestParam(required = false) String endDate) {
        
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
        
        LearningGoal goal = learningGoalService.createGoal(userId, subject, dailyTargetMinutes, start, end);
        return ResponseEntity.ok(goal);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<List<LearningGoal>> getGoals(@PathVariable Long userId) {
        List<LearningGoal> goals = learningGoalService.findByUserId(userId);
        return ResponseEntity.ok(goals);
    }
    
    @GetMapping("/{userId}/active")
    public ResponseEntity<List<LearningGoal>> getActiveGoals(@PathVariable Long userId) {
        List<LearningGoal> goals = learningGoalService.findActiveGoalsByUserId(userId);
        return ResponseEntity.ok(goals);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LearningGoal> getGoal(@PathVariable Long id) {
        return learningGoalService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LearningGoal> updateGoal(@PathVariable Long id, @RequestBody LearningGoal goal) {
        return learningGoalService.findById(id)
                .map(existingGoal -> {
                    goal.setId(id);
                    goal.setUser(existingGoal.getUser());
                    LearningGoal updatedGoal = learningGoalService.save(goal);
                    return ResponseEntity.ok(updatedGoal);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateGoal(@PathVariable Long id) {
        learningGoalService.deactivateGoal(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        learningGoalService.deleteById(id);
        return ResponseEntity.ok().build();
    }
} 
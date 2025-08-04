package com.studyapp.controller;

import com.studyapp.domain.StudyLog;
import com.studyapp.dto.WeeklyProgressDto;
import com.studyapp.service.StudyLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class StudyLogController {
    
    @Autowired
    private StudyLogService studyLogService;
    
    @PostMapping
    public ResponseEntity<StudyLog> logStudy(@RequestBody StudyLog log) {
        StudyLog savedLog = studyLogService.save(log);
        return ResponseEntity.ok(savedLog);
    }
    
    @PostMapping("/log")
    public ResponseEntity<StudyLog> logStudyWithParams(
            @RequestParam Long userId,
            @RequestParam String studyDate,
            @RequestParam Integer minutesStudied,
            @RequestParam(required = false) String notes) {
        
        LocalDate date = LocalDate.parse(studyDate);
        StudyLog log = studyLogService.logStudy(userId, date, minutesStudied, notes);
        return ResponseEntity.ok(log);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<List<StudyLog>> getLogs(@PathVariable Long userId) {
        List<StudyLog> logs = studyLogService.findByUserId(userId);
        return ResponseEntity.ok(logs);
    }
    
    @GetMapping("/{userId}/weekly")
    public ResponseEntity<List<WeeklyProgressDto>> getWeeklyProgress(@PathVariable Long userId) {
        List<WeeklyProgressDto> weeklyProgress = studyLogService.getWeeklyProgress(userId);
        return ResponseEntity.ok(weeklyProgress);
    }
    
    @GetMapping("/{userId}/range")
    public ResponseEntity<List<StudyLog>> getLogsByDateRange(
            @PathVariable Long userId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        List<StudyLog> logs = studyLogService.findByUserIdAndDateRange(userId, start, end);
        return ResponseEntity.ok(logs);
    }
    
    @GetMapping("/{userId}/total")
    public ResponseEntity<Integer> getTotalStudyMinutes(
            @PathVariable Long userId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        Integer totalMinutes = studyLogService.getTotalStudyMinutes(userId, start, end);
        return ResponseEntity.ok(totalMinutes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<StudyLog> getLog(@PathVariable Long id) {
        return studyLogService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<StudyLog> updateLog(@PathVariable Long id, @RequestBody StudyLog log) {
        return studyLogService.findById(id)
                .map(existingLog -> {
                    log.setId(id);
                    log.setUser(existingLog.getUser());
                    StudyLog updatedLog = studyLogService.save(log);
                    return ResponseEntity.ok(updatedLog);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        studyLogService.deleteById(id);
        return ResponseEntity.ok().build();
    }
} 
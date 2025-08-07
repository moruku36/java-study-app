package com.studyapp.controller;

import com.studyapp.domain.StudyLog;
import com.studyapp.dto.WeeklyProgressDto;
import com.studyapp.service.StudyLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// エラーレスポンス用の内部クラス
class ErrorResponse {
    private String error;
    
    public ErrorResponse(String error) {
        this.error = error;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
}

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
    public ResponseEntity<?> logStudyWithParams(
            @RequestParam Long userId,
            @RequestParam String studyDate,
            @RequestParam Integer minutesStudied,
            @RequestParam(required = false) String notes) {
        
        try {
            // パラメータのバリデーション
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("ユーザーIDが指定されていません"));
            }
            if (studyDate == null || studyDate.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("学習日が指定されていません"));
            }
            if (minutesStudied == null || minutesStudied < 1 || minutesStudied > 1440) {
                return ResponseEntity.badRequest().body(new ErrorResponse("学習時間は1分〜1440分の間で指定してください"));
            }
            
            LocalDate date = LocalDate.parse(studyDate);
            StudyLog log = studyLogService.logStudy(userId, date, minutesStudied, notes);
            return ResponseEntity.ok(log);
        } catch (java.time.format.DateTimeParseException e) {
            System.err.println("日付パースエラー: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse("日付の形式が正しくありません"));
        } catch (IllegalArgumentException e) {
            System.err.println("バリデーションエラー: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.err.println("学習記録保存エラー: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ErrorResponse("サーバーエラーが発生しました。しばらく時間をおいて再度お試しください"));
        }
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
package com.studyapp.controller;

import com.studyapp.constant.AppConstants;
import com.studyapp.service.StudyLogService;
import com.studyapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {
    
    private final UserService userService;
    private final StudyLogService studyLogService;
    
    public HealthController(UserService userService, StudyLogService studyLogService) {
        this.userService = userService;
        this.studyLogService = studyLogService;
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("application", AppConstants.APP_NAME);
        response.put("version", AppConstants.APP_VERSION);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
    
    @GetMapping("/db-test")
    public ResponseEntity<Map<String, Object>> databaseTest() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // ユーザー数を取得
            long userCount = userService.count();
            response.put("userCount", userCount);
            
            // 学習記録数を取得
            long studyLogCount = studyLogService.count();
            response.put("studyLogCount", studyLogCount);
            
            // 今日の学習記録を取得
            var todayLogs = studyLogService.findByUserIdAndDateRange(1L, 
                java.time.LocalDate.now(), java.time.LocalDate.now());
            response.put("todayLogsCount", todayLogs != null ? todayLogs.size() : 0);
            
            response.put("status", "SUCCESS");
            response.put("message", "データベース接続正常");
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "データベース接続エラー: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(response);
    }
} 
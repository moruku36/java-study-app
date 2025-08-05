package com.studyapp.config;

import com.studyapp.domain.LearningGoal;
import com.studyapp.domain.StudyLog;
import com.studyapp.domain.User;
import com.studyapp.service.LearningGoalService;
import com.studyapp.service.StudyLogService;
import com.studyapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Value("${spring.data.init.enabled:true}")
    private boolean dataInitEnabled;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private LearningGoalService learningGoalService;
    
    @Autowired
    private StudyLogService studyLogService;
    
    @Override
    public void run(String... args) throws Exception {
        if (!dataInitEnabled) {
            System.out.println("データ初期化が無効化されています");
            return;
        }
        
        try {
            System.out.println("=== データ初期化開始 ===");
            
            // サンプルユーザーを作成
            User sampleUser = createSampleUser();
            System.out.println("ユーザー作成完了: " + sampleUser.getUsername());
            
            // サンプル学習目標を作成
            createSampleGoals(sampleUser);
            System.out.println("学習目標作成完了");
            
            // サンプル学習記録を作成
            createSampleStudyLogs(sampleUser);
            System.out.println("学習記録作成完了");
            
            System.out.println("=== データ初期化完了 ===");
        } catch (Exception e) {
            System.err.println("データ初期化エラー: " + e.getMessage());
            System.err.println("エラーの詳細:");
            e.printStackTrace();
            System.err.println("アプリケーションを継続します...");
            // エラーが発生してもアプリケーションを継続（エラーを再スローしない）
        }
    }
    
    private User createSampleUser() {
        // 既存のユーザーをチェック
        if (userService.findByUsername("sample_user").isPresent()) {
            return userService.findByUsername("sample_user").get();
        }
        
        User user = new User("sample_user", "sample@example.com", "password123");
        return userService.save(user);
    }
    
    private void createSampleGoals(User user) {
        // 既存の目標をチェック
        if (!learningGoalService.findByUserId(user.getId()).isEmpty()) {
            return;
        }
        
        // Java学習目標
        LearningGoal javaGoal = learningGoalService.createGoal(
            user.getId(), 
            "Java", 
            60, 
            LocalDate.now().minusDays(7), 
            LocalDate.now().plusDays(30)
        );
        
        // 数学学習目標
        LearningGoal mathGoal = learningGoalService.createGoal(
            user.getId(), 
            "数学", 
            45, 
            LocalDate.now().minusDays(5), 
            LocalDate.now().plusDays(20)
        );
        
        // 英語学習目標
        LearningGoal englishGoal = learningGoalService.createGoal(
            user.getId(), 
            "英語", 
            30, 
            LocalDate.now().minusDays(3), 
            null
        );
    }
    
    private void createSampleStudyLogs(User user) {
        // 既存の記録をチェック
        if (!studyLogService.findByUserId(user.getId()).isEmpty()) {
            return;
        }
        
        LocalDate today = LocalDate.now();
        
        // 過去7日間のサンプルデータ
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            
            // ランダムな学習時間（30-120分）
            int minutes = 30 + (int)(Math.random() * 90);
            
            // 学習記録を作成
            studyLogService.logStudy(
                user.getId(),
                date,
                minutes,
                "サンプル学習記録 - " + date.toString()
            );
        }
    }
} 
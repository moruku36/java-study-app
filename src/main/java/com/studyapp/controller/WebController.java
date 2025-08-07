package com.studyapp.controller;

import com.studyapp.domain.LearningGoal;
import com.studyapp.domain.StudyLog;
import com.studyapp.domain.User;
import com.studyapp.dto.WeeklyProgressDto;
import com.studyapp.service.LearningGoalService;
import com.studyapp.service.StudyLogService;
import com.studyapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.studyapp.constant.AppConstants;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Controller
public class WebController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebController.class);
    
    private final UserService userService;
    private final LearningGoalService learningGoalService;
    private final StudyLogService studyLogService;
    
    public WebController(UserService userService, 
                        LearningGoalService learningGoalService, 
                        StudyLogService studyLogService) {
        this.userService = userService;
        this.learningGoalService = learningGoalService;
        this.studyLogService = studyLogService;
    }
    
    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "1") Long userId, Model model) {
        try {
            User user = getUserOrCreateDefault(userId);
            model.addAttribute("user", user);
            
            // アクティブな学習目標を取得
            List<LearningGoal> activeGoals = learningGoalService.findActiveGoalsByUserId(userId);
            model.addAttribute("activeGoals", activeGoals != null ? activeGoals : new ArrayList<>());
            
            // 週次進捗を取得
            List<WeeklyProgressDto> weeklyProgress = studyLogService.getWeeklyProgress(userId);
            model.addAttribute("weeklyProgress", weeklyProgress != null ? weeklyProgress : new ArrayList<>());
            
            // 今日の学習記録を取得
            LocalDate today = LocalDate.now();
            List<StudyLog> todayLogs = studyLogService.findByUserIdAndDateRange(userId, today, today);
            model.addAttribute("todayLogs", todayLogs != null ? todayLogs : new ArrayList<>());
            
            // 今週の総学習時間を計算
            Integer weeklyTotal = studyLogService.getTotalStudyMinutes(userId, 
                today.with(java.time.DayOfWeek.SUNDAY), 
                today.with(java.time.DayOfWeek.SATURDAY));
            model.addAttribute("weeklyTotal", weeklyTotal != null ? weeklyTotal : 0);
            
            // エラーフラグをクリア
            model.addAttribute("hasError", false);
            
        } catch (Exception e) {
            // エラーの詳細をログに出力
            logger.error("ダッシュボードのデータ取得中にエラーが発生しました。", e);
            
            // エラー情報をモデルに追加
            model.addAttribute("hasError", true);
            model.addAttribute("errorMessage", "データの取得中にエラーが発生しました。しばらく時間をおいて再度お試しください。");
            
            // フォールバックデータを設定
            User user = getUserOrCreateDefault(userId);
            model.addAttribute("user", user);
            model.addAttribute("activeGoals", new ArrayList<>());
            model.addAttribute("weeklyProgress", new ArrayList<>());
            model.addAttribute("todayLogs", new ArrayList<>());
            model.addAttribute("weeklyTotal", 0);
        }
        
        return "dashboard";
    }
    
    @GetMapping("/goals")
    public String goals(@RequestParam(defaultValue = "1") Long userId, Model model) {
        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            List<User> users = userService.findAll();
            if (!users.isEmpty()) {
                user = users.get(0);
                userId = user.getId();
            }
        }
        if (user != null) {
            model.addAttribute("user", user);
            List<LearningGoal> goals = learningGoalService.findByUserId(userId);
            model.addAttribute("goals", goals);
            model.addAttribute("newGoal", new LearningGoal());
        }
        
        return "goals";
    }
    
    @GetMapping("/log")
    public String logForm(@RequestParam(defaultValue = "1") Long userId, Model model) {
        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            List<User> users = userService.findAll();
            if (!users.isEmpty()) {
                user = users.get(0);
                userId = user.getId();
            }
        }
        if (user != null) {
            model.addAttribute("user", user);
        }
        
        model.addAttribute("studyLog", new StudyLog());
        model.addAttribute("today", LocalDate.now());
        
        return "log";
    }
    
    @GetMapping("/history")
    public String history(@RequestParam(defaultValue = "1") Long userId, 
                         @RequestParam(required = false) String startDate,
                         @RequestParam(required = false) String endDate,
                         Model model) {
        try {
            User user = userService.findById(userId).orElse(null);
            if (user == null) {
                List<User> users = userService.findAll();
                if (!users.isEmpty()) {
                    user = users.get(0);
                    userId = user.getId();
                } else {
                    // ユーザーが存在しない場合はデフォルトユーザーを作成
                    user = new User();
                    user.setUsername("sample_user");
                    user.setEmail("sample@example.com");
                    user.setPassword("password123");
                    user = userService.save(user);
                    userId = user.getId();
                }
            }
            
            model.addAttribute("user", user);
            
            // 日付のパース処理を安全に行う
            LocalDate start;
            LocalDate end;
            
            try {
                start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusWeeks(1);
            } catch (Exception e) {
                start = LocalDate.now().minusWeeks(1);
            }
            
            try {
                end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
            } catch (Exception e) {
                end = LocalDate.now();
            }
            
            List<StudyLog> logs = studyLogService.findByUserIdAndDateRange(userId, start, end);
            model.addAttribute("logs", logs != null ? logs : new ArrayList<>());
            model.addAttribute("startDate", start);
            model.addAttribute("endDate", end);
            
        } catch (Exception e) {
            // エラーの詳細をログに出力
            logger.error("学習履歴の取得中にエラーが発生しました。", e);
            
            // エラー情報をモデルに追加
            model.addAttribute("status", "500");
            model.addAttribute("error", e.getClass().getSimpleName());
            model.addAttribute("message", e.getMessage());
            model.addAttribute("stackTrace", getStackTraceAsString(e));
            
            // フォールバックデータを設定
            model.addAttribute("logs", new ArrayList<>());
            model.addAttribute("startDate", LocalDate.now().minusWeeks(1));
            model.addAttribute("endDate", LocalDate.now());
            
            // エラーページを直接表示
            return "error";
        }
        
        return "history";
    }
    
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/log")
    public String saveStudyLog(@RequestParam Long userId,
                              @RequestParam String studyDate,
                              @RequestParam Integer minutesStudied,
                              @RequestParam(required = false) String notes,
                              Model model) {
        try {
            User user = userService.findById(userId).orElse(null);
            if (user == null) {
                model.addAttribute("error", "ユーザーが見つかりません");
                return "log";
            }
            
            LocalDate date = LocalDate.parse(studyDate);
            StudyLog savedLog = studyLogService.logStudy(userId, date, minutesStudied, notes);
            
            model.addAttribute("success", "学習記録を保存しました");
            model.addAttribute("user", user);
            model.addAttribute("studyLog", new StudyLog());
            model.addAttribute("today", LocalDate.now());
            
        } catch (Exception e) {
            // エラーの詳細をログに出力
            logger.error("学習記録の保存中にエラーが発生しました。", e);
            
            model.addAttribute("error", "学習記録の保存中にエラーが発生しました: " + e.getMessage());
            model.addAttribute("user", userService.findById(userId).orElse(null));
            model.addAttribute("studyLog", new StudyLog());
            model.addAttribute("today", LocalDate.now());
        }
        
        return "log";
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, 
                              @RequestParam String confirmPassword,
                              Model model) {
        try {
            // バリデーション
            if (user.getUsername() == null || user.getUsername().trim().length() < 3) {
                model.addAttribute("error", "ユーザー名は3文字以上で入力してください");
                return "register";
            }
            
            if (user.getEmail() == null || !user.getEmail().contains("@")) {
                model.addAttribute("error", "有効なメールアドレスを入力してください");
                return "register";
            }
            
            if (user.getPassword() == null || user.getPassword().length() < 6) {
                model.addAttribute("error", "パスワードは6文字以上で入力してください");
                return "register";
            }
            
            if (!user.getPassword().equals(confirmPassword)) {
                model.addAttribute("error", "パスワードが一致しません");
                return "register";
            }
            
            // ユーザー名とメールアドレスの重複チェック
            if (userService.existsByUsername(user.getUsername())) {
                model.addAttribute("error", "このユーザー名は既に使用されています");
                return "register";
            }
            
            if (userService.existsByEmail(user.getEmail())) {
                model.addAttribute("error", "このメールアドレスは既に使用されています");
                return "register";
            }
            
            // ユーザーを保存
            User savedUser = userService.save(user);
            
            model.addAttribute("success", "ユーザー登録が完了しました。ログインしてください。");
            return "redirect:/login";
            
        } catch (Exception e) {
            // エラーの詳細をログに出力
            logger.error("ユーザー登録中にエラーが発生しました。", e);
            
            model.addAttribute("error", "ユーザー登録中にエラーが発生しました: " + e.getMessage());
            return "register";
        }
    }
    
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
    
    /**
     * ユーザーを取得またはデフォルトユーザーを作成
     */
    private User getUserOrCreateDefault(Long userId) {
        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            // ユーザーが存在しない場合は最初のユーザーを取得
            List<User> users = userService.findAll();
            if (!users.isEmpty()) {
                user = users.get(0);
            } else {
                // ユーザーが存在しない場合はデフォルトユーザーを作成
                user = createDefaultUser();
            }
        }
        return user;
    }
    
    /**
     * デフォルトユーザーを作成
     */
    private User createDefaultUser() {
        User user = new User();
        user.setUsername(AppConstants.DEFAULT_USERNAME);
        user.setEmail(AppConstants.DEFAULT_EMAIL);
        user.setPassword(AppConstants.DEFAULT_PASSWORD);
        return userService.save(user);
    }
    
    /**
     * 例外のスタックトレースを文字列として取得
     */
    private String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.toString()).append("\n");
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }
} 
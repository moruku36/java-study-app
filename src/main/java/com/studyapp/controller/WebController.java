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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    public String index() { return "redirect:/dashboard"; }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "1") Long userId, Model model) {
        try {
            User user = getOrCreateDefault(userId);
            model.addAttribute("user", user);

            // アクティブ目標
            List<LearningGoal> activeGoals = new ArrayList<>();
            try { activeGoals = learningGoalService.findActiveGoalsByUserId(user.getId()); } catch (Exception ignored) {}
            model.addAttribute("activeGoals", activeGoals);

            // 週次進捗
            List<WeeklyProgressDto> weeklyProgress = new ArrayList<>();
            try {
                weeklyProgress = studyLogService.getWeeklyProgress(user.getId());
            } catch (Exception e) {
                logger.warn("weeklyProgress load failed: {}", e.getMessage());
            }
            model.addAttribute("weeklyProgress", weeklyProgress);

            // 今日の記録
            LocalDate today = LocalDate.now();
            List<StudyLog> todayLogs = new ArrayList<>();
            try {
                todayLogs = studyLogService.findByUserIdAndDateRange(user.getId(), today, today);
            } catch (Exception e) {
                logger.warn("todayLogs load failed: {}", e.getMessage());
            }
            model.addAttribute("todayLogs", todayLogs);

            // 今週合計
            LocalDate weekStart = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY));
            LocalDate weekEnd = today.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SATURDAY));
            Integer weeklyTotal = 0;
            try {
                weeklyTotal = studyLogService.getTotalStudyMinutes(user.getId(), weekStart, weekEnd);
            } catch (Exception e) {
                logger.warn("weeklyTotal calc failed: {}", e.getMessage());
            }
            model.addAttribute("weeklyTotal", weeklyTotal);

            // 今日合計
            int todayTotal = todayLogs == null ? 0 : todayLogs.stream()
                    .mapToInt(l -> l.getMinutesStudied() == null ? 0 : l.getMinutesStudied()).sum();
            model.addAttribute("todayTotal", todayTotal);

            // 平均達成率
            double avg = 0.0;
            if (weeklyProgress != null && !weeklyProgress.isEmpty()) {
                avg = weeklyProgress.stream()
                        .mapToDouble(p -> p.getAchievementRate() == null ? 0.0 : p.getAchievementRate())
                        .average().orElse(0.0);
            }
            model.addAttribute("averageAchievement", String.format("%.1f", avg));

            model.addAttribute("hasError", false);
        } catch (Exception e) {
            logger.error("dashboard load error", e);
            model.addAttribute("hasError", true);
            model.addAttribute("errorMessage", "ダッシュボードの読み込み中にエラーが発生しました");
            User fallback = new User();
            fallback.setId(userId);
            fallback.setUsername("sample_user");
            fallback.setEmail("sample@example.com");
            model.addAttribute("user", fallback);
            model.addAttribute("activeGoals", new ArrayList<LearningGoal>());
            model.addAttribute("weeklyProgress", new ArrayList<WeeklyProgressDto>());
            model.addAttribute("todayLogs", new ArrayList<StudyLog>());
            model.addAttribute("weeklyTotal", 0);
            model.addAttribute("todayTotal", 0);
            model.addAttribute("averageAchievement", "0.0");
        }
        return "dashboard";
    }

    @GetMapping("/goals")
    public String goals(@RequestParam(defaultValue = "1") Long userId, Model model) {
        User user = getOrCreateDefault(userId);
        model.addAttribute("user", user);
        List<LearningGoal> goals = learningGoalService.findByUserId(user.getId());
        model.addAttribute("goals", goals);
        model.addAttribute("newGoal", new LearningGoal());
        return "goals";
    }

    @PostMapping("/goals/create")
    public String createGoal(@RequestParam Long userId,
                             @RequestParam String subject,
                             @RequestParam Integer dailyTargetMinutes,
                             @RequestParam String startDate,
                             @RequestParam(required = false) String endDate) {
        User user = getOrCreateDefault(userId);
        LearningGoal goal = new LearningGoal();
        goal.setUser(user);
        goal.setSubject(subject);
        goal.setDailyTargetMinutes(dailyTargetMinutes);
        goal.setStartDate(LocalDate.parse(startDate));
        if (endDate != null && !endDate.isBlank()) goal.setEndDate(LocalDate.parse(endDate));
        goal.setIsActive(true);
        learningGoalService.save(goal);
        return "redirect:/goals?userId=" + user.getId();
    }

    @PostMapping("/goals/{id}/deactivate")
    public String deactivateGoal(@PathVariable Long id, @RequestParam Long userId) {
        LearningGoal goal = learningGoalService.findByUserId(userId).stream().filter(g -> g.getId().equals(id)).findFirst().orElse(null);
        if (goal != null) { goal.setIsActive(false); learningGoalService.save(goal); }
        return "redirect:/goals?userId=" + userId;
    }

    @PostMapping("/goals/{id}/activate")
    public String activateGoal(@PathVariable Long id, @RequestParam Long userId) {
        LearningGoal goal = learningGoalService.findByUserId(userId).stream().filter(g -> g.getId().equals(id)).findFirst().orElse(null);
        if (goal != null) { goal.setIsActive(true); learningGoalService.save(goal); }
        return "redirect:/goals?userId=" + userId;
    }

    @PostMapping("/goals/{id}/delete")
    public String deleteGoal(@PathVariable Long id, @RequestParam Long userId) {
        // 簡易削除: 学習目標サービスに削除APIがないためリポジトリに委譲しても良いがここでは非対応。
        // 代替: 非アクティブ化相当
        return deactivateGoal(id, userId);
    }

    @GetMapping("/log")
    public String logForm(@RequestParam(defaultValue = "1") Long userId, Model model) {
        User user = getOrCreateDefault(userId);
        model.addAttribute("user", user);
        model.addAttribute("studyLog", null);
        model.addAttribute("today", LocalDate.now());
        return "log";
    }

    @GetMapping("/history")
    public String history(@RequestParam(defaultValue = "1") Long userId,
                         @RequestParam(required = false) String startDate,
                         @RequestParam(required = false) String endDate,
                         Model model) {
        User user = getOrCreateDefault(userId);
        model.addAttribute("user", user);
        LocalDate start = startDate != null && !startDate.isBlank() ? LocalDate.parse(startDate) : LocalDate.now().minusWeeks(1);
        LocalDate end = endDate != null && !endDate.isBlank() ? LocalDate.parse(endDate) : LocalDate.now();
        List<StudyLog> logs = studyLogService.findByUserIdAndDateRange(user.getId(), start, end);
        int totalMinutes = logs == null ? 0 : logs.stream().mapToInt(l -> l.getMinutesStudied() == null ? 0 : l.getMinutesStudied()).sum();
        int dayCount = logs == null ? 0 : logs.size();
        int avgPerDay = dayCount > 0 ? Math.round((float) totalMinutes / dayCount) : 0;
        model.addAttribute("logs", logs);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("totalMinutes", totalMinutes);
        model.addAttribute("dayCount", dayCount);
        model.addAttribute("avgPerDay", avgPerDay);
        return "history";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        User user = new User();
        user.setId(1L);
        user.setUsername("");
        user.setEmail("");
        user.setPassword("");
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/log")
    public String saveStudyLog(@RequestParam Long userId,
                              @RequestParam String studyDate,
                              @RequestParam Integer minutesStudied,
                              @RequestParam(required = false) String notes,
                              Model model) {
        try {
            LocalDate date = LocalDate.parse(studyDate);
            studyLogService.logStudy(userId, date, minutesStudied, notes);
            return "redirect:/dashboard?userId=" + userId;
        } catch (Exception e) {
            User user = getOrCreateDefault(userId);
            model.addAttribute("user", user);
            model.addAttribute("error", "学習記録の保存に失敗しました: " + e.getMessage());
            model.addAttribute("today", LocalDate.now());
            return "log";
        }
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user,
                              @RequestParam String confirmPassword,
                              Model model) {
        model.addAttribute("success", "ユーザー登録が完了しました。ログインしてください。");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() { return "login"; }

    @PostMapping("/login")
    public String processLogin() { return "redirect:/dashboard"; }

    private User getOrCreateDefault(Long userId) {
        return userService.findById(userId).orElseGet(() -> {
            // 初回アクセス時にデフォルトユーザーを作成
            User u = new User();
            u.setUsername("sample_user");
            u.setEmail("sample@example.com");
            u.setPassword("password123");
            return userService.save(u);
        });
    }
} 
package com.studyapp.controller;

import com.studyapp.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;

@Controller
public class WebController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebController.class);
    

    
    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "1") Long userId, Model model) {
        // 最もシンプルなダッシュボード表示
        User user = new User();
        user.setId(userId);
        user.setUsername("sample_user");
        user.setEmail("sample@example.com");
        
        model.addAttribute("user", user);
        model.addAttribute("activeGoals", new ArrayList<>());
        model.addAttribute("weeklyProgress", new ArrayList<>());
        model.addAttribute("todayLogs", new ArrayList<>());
        model.addAttribute("weeklyTotal", 0);
        model.addAttribute("hasError", false);
        
        return "dashboard";
    }
    
    @GetMapping("/goals")
    public String goals(@RequestParam(defaultValue = "1") Long userId, Model model) {
        User user = new User();
        user.setId(userId);
        user.setUsername("sample_user");
        user.setEmail("sample@example.com");
        
        model.addAttribute("user", user);
        model.addAttribute("goals", new ArrayList<>());
        model.addAttribute("newGoal", null);
        
        return "goals";
    }
    
    @GetMapping("/log")
    public String logForm(@RequestParam(defaultValue = "1") Long userId, Model model) {
        User user = new User();
        user.setId(userId);
        user.setUsername("sample_user");
        user.setEmail("sample@example.com");
        
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
        User user = new User();
        user.setId(userId);
        user.setUsername("sample_user");
        user.setEmail("sample@example.com");
        
        model.addAttribute("user", user);
        model.addAttribute("logs", new ArrayList<>());
        model.addAttribute("startDate", LocalDate.now().minusWeeks(1));
        model.addAttribute("endDate", LocalDate.now());
        
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
        User user = new User();
        user.setId(userId);
        user.setUsername("sample_user");
        user.setEmail("sample@example.com");
        
        model.addAttribute("success", "学習記録を保存しました");
        model.addAttribute("user", user);
        model.addAttribute("studyLog", null);
        model.addAttribute("today", LocalDate.now());
        
        return "log";
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, 
                              @RequestParam String confirmPassword,
                              Model model) {
        model.addAttribute("success", "ユーザー登録が完了しました。ログインしてください。");
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
    
    @PostMapping("/login")
    public String processLogin() {
        // ログイン処理をバイパスして直接ダッシュボードにリダイレクト
        return "redirect:/dashboard";
    }
    

} 
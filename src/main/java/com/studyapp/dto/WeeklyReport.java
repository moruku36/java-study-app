package com.studyapp.dto;

import java.time.LocalDate;

public class WeeklyReport {
    
    private Long userId;
    private String username;
    private LocalDate reportDate;
    private Integer totalMinutes;
    private Double achievementRate;
    
    public WeeklyReport() {}
    
    public WeeklyReport(Long userId, String username, LocalDate reportDate, Integer totalMinutes, Double achievementRate) {
        this.userId = userId;
        this.username = username;
        this.reportDate = reportDate;
        this.totalMinutes = totalMinutes;
        this.achievementRate = achievementRate;
    }
    
    // Getter/Setter
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public LocalDate getReportDate() {
        return reportDate;
    }
    
    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }
    
    public Integer getTotalMinutes() {
        return totalMinutes;
    }
    
    public void setTotalMinutes(Integer totalMinutes) {
        this.totalMinutes = totalMinutes;
    }
    
    public Double getAchievementRate() {
        return achievementRate;
    }
    
    public void setAchievementRate(Double achievementRate) {
        this.achievementRate = achievementRate;
    }
} 
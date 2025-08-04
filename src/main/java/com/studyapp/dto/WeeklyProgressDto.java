package com.studyapp.dto;

import java.time.LocalDate;

public class WeeklyProgressDto {
    
    private LocalDate date;
    private Integer minutesStudied;
    private Integer targetMinutes;
    private Double achievementRate;
    private String dayOfWeek;
    
    public WeeklyProgressDto() {}
    
    public WeeklyProgressDto(LocalDate date, Integer minutesStudied, Integer targetMinutes) {
        this.date = date;
        this.minutesStudied = minutesStudied != null ? minutesStudied : 0;
        this.targetMinutes = targetMinutes != null ? targetMinutes : 0;
        this.dayOfWeek = date.getDayOfWeek().toString();
        this.achievementRate = calculateAchievementRate();
    }
    
    private Double calculateAchievementRate() {
        if (targetMinutes == 0) {
            return 0.0;
        }
        return (double) minutesStudied / targetMinutes * 100;
    }
    
    // Getter/Setter
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public Integer getMinutesStudied() {
        return minutesStudied;
    }
    
    public void setMinutesStudied(Integer minutesStudied) {
        this.minutesStudied = minutesStudied;
    }
    
    public Integer getTargetMinutes() {
        return targetMinutes;
    }
    
    public void setTargetMinutes(Integer targetMinutes) {
        this.targetMinutes = targetMinutes;
    }
    
    public Double getAchievementRate() {
        return achievementRate;
    }
    
    public void setAchievementRate(Double achievementRate) {
        this.achievementRate = achievementRate;
    }
    
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
} 
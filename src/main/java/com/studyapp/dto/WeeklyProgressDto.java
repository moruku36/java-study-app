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
        this.minutesStudied = minutesStudied == null ? 0 : minutesStudied;
        this.targetMinutes = targetMinutes == null ? 0 : targetMinutes;
        this.dayOfWeek = date.getDayOfWeek().toString();
        this.achievementRate = calculate();
    }

    private Double calculate() {
        if (targetMinutes == 0) return 0.0;
        return (double) minutesStudied / targetMinutes * 100;
    }

    public LocalDate getDate() { return date; }
    public Integer getMinutesStudied() { return minutesStudied; }
    public Integer getTargetMinutes() { return targetMinutes; }
    public Double getAchievementRate() { return achievementRate; }
    public String getDayOfWeek() { return dayOfWeek; }

    public void setDate(LocalDate date) { this.date = date; }
    public void setMinutesStudied(Integer minutesStudied) { this.minutesStudied = minutesStudied; }
    public void setTargetMinutes(Integer targetMinutes) { this.targetMinutes = targetMinutes; }
    public void setAchievementRate(Double achievementRate) { this.achievementRate = achievementRate; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
}

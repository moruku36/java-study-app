package com.studyapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "study_logs")
public class StudyLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull(message = "学習日は必須です")
    @Column(name = "study_date", nullable = false)
    private LocalDate studyDate;
    
    @NotNull(message = "学習時間は必須です")
    @Min(value = 1, message = "学習時間は1分以上で入力してください")
    @Column(name = "minutes_studied", nullable = false)
    private Integer minutesStudied;
    
    @Column(name = "notes", length = 1000)
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // コンストラクタ
    public StudyLog() {}
    
    public StudyLog(User user, LocalDate studyDate, Integer minutesStudied) {
        this.user = user;
        this.studyDate = studyDate;
        this.minutesStudied = minutesStudied;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public StudyLog(User user, LocalDate studyDate, Integer minutesStudied, String notes) {
        this.user = user;
        this.studyDate = studyDate;
        this.minutesStudied = minutesStudied;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getter/Setter
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDate getStudyDate() {
        return studyDate;
    }
    
    public void setStudyDate(LocalDate studyDate) {
        this.studyDate = studyDate;
    }
    
    public Integer getMinutesStudied() {
        return minutesStudied;
    }
    
    public void setMinutesStudied(Integer minutesStudied) {
        this.minutesStudied = minutesStudied;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // エンティティライフサイクルメソッド
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 
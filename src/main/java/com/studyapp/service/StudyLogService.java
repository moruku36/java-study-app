package com.studyapp.service;

import com.studyapp.domain.LearningGoal;
import com.studyapp.domain.StudyLog;
import com.studyapp.domain.User;
import com.studyapp.dto.WeeklyProgressDto;
import com.studyapp.repository.LearningGoalRepository;
import com.studyapp.repository.StudyLogRepository;
import com.studyapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudyLogService {
    
    private final StudyLogRepository studyLogRepository;
    private final UserRepository userRepository;
    private final LearningGoalRepository learningGoalRepository;
    
    public StudyLogService(StudyLogRepository studyLogRepository,
                          UserRepository userRepository,
                          LearningGoalRepository learningGoalRepository) {
        this.studyLogRepository = studyLogRepository;
        this.userRepository = userRepository;
        this.learningGoalRepository = learningGoalRepository;
    }
    
    public List<StudyLog> findAll() {
        return studyLogRepository.findAll();
    }
    
    public Optional<StudyLog> findById(Long id) {
        return studyLogRepository.findById(id);
    }
    
    public List<StudyLog> findByUserId(Long userId) {
        return studyLogRepository.findByUserId(userId);
    }
    
    public List<StudyLog> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return studyLogRepository.findByUserIdAndStudyDateBetween(userId, startDate, endDate);
    }
    
    public StudyLog save(StudyLog studyLog) {
        return studyLogRepository.save(studyLog);
    }
    
    public StudyLog logStudy(Long userId, LocalDate studyDate, Integer minutesStudied, String notes) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("ユーザーが見つかりません: " + userId);
        }
        
        // 同じ日の記録があるかチェック
        Optional<StudyLog> existingLog = studyLogRepository.findByUserIdAndStudyDate(userId, studyDate);
        if (existingLog.isPresent()) {
            // 既存の記録を更新
            StudyLog log = existingLog.get();
            log.setMinutesStudied(log.getMinutesStudied() + minutesStudied);
            if (notes != null && !notes.trim().isEmpty()) {
                String currentNotes = log.getNotes();
                log.setNotes(currentNotes != null ? currentNotes + "\n" + notes : notes);
            }
            return studyLogRepository.save(log);
        } else {
            // 新しい記録を作成
            StudyLog newLog = new StudyLog(userOpt.get(), studyDate, minutesStudied, notes);
            return studyLogRepository.save(newLog);
        }
    }
    
    public List<WeeklyProgressDto> getWeeklyProgress(Long userId) {
        try {
            LocalDate today = LocalDate.now();
            LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY));
            LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SATURDAY));
            
            List<WeeklyProgressDto> weeklyProgress = new ArrayList<>();
            
            // アクティブな目標を取得
            List<LearningGoal> activeGoals = learningGoalRepository.findActiveGoalsByUserId(userId);
            Integer targetMinutes = activeGoals != null ? activeGoals.stream()
                    .mapToInt(goal -> goal.getDailyTargetMinutes() != null ? goal.getDailyTargetMinutes() : 0)
                    .sum() : 0;
            
            // 週の各日について進捗を計算
            for (LocalDate date = weekStart; !date.isAfter(weekEnd); date = date.plusDays(1)) {
                Integer minutesStudied = studyLogRepository.sumMinutesByUserIdAndStudyDate(userId, date);
                if (minutesStudied == null) {
                    minutesStudied = 0;
                }
                
                WeeklyProgressDto progress = new WeeklyProgressDto(date, minutesStudied, targetMinutes);
                weeklyProgress.add(progress);
            }
            
            return weeklyProgress;
        } catch (Exception e) {
            System.err.println("週次進捗取得エラー: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public Integer getTotalStudyMinutes(Long userId, LocalDate startDate, LocalDate endDate) {
        try {
            List<StudyLog> logs = studyLogRepository.findByUserIdAndStudyDateBetween(userId, startDate, endDate);
            return logs != null ? logs.stream()
                    .mapToInt(log -> log.getMinutesStudied() != null ? log.getMinutesStudied() : 0)
                    .sum() : 0;
        } catch (Exception e) {
            System.err.println("総学習時間取得エラー: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    public void deleteById(Long id) {
        studyLogRepository.deleteById(id);
    }
    
    public long count() {
        return studyLogRepository.count();
    }
} 
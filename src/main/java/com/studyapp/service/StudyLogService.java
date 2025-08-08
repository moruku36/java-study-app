package com.studyapp.service;

import com.studyapp.domain.LearningGoal;
import com.studyapp.domain.StudyLog;
import com.studyapp.domain.User;
import com.studyapp.dto.WeeklyProgressDto;
import com.studyapp.repository.LearningGoalRepository;
import com.studyapp.repository.StudyLogRepository;
import com.studyapp.repository.UserRepository;
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

    public StudyLogService(StudyLogRepository studyLogRepository, UserRepository userRepository, LearningGoalRepository learningGoalRepository) {
        this.studyLogRepository = studyLogRepository;
        this.userRepository = userRepository;
        this.learningGoalRepository = learningGoalRepository;
    }

    public List<StudyLog> findByUserId(Long userId) { return studyLogRepository.findByUserId(userId); }

    public List<StudyLog> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return studyLogRepository.findByUserIdAndStudyDateBetween(userId, startDate, endDate);
    }

    public StudyLog save(StudyLog studyLog) { return studyLogRepository.save(studyLog); }

    public StudyLog logStudy(Long userId, LocalDate studyDate, Integer minutesStudied, String notes) {
        if (userId == null || studyDate == null || minutesStudied == null || minutesStudied < 1) {
            throw new IllegalArgumentException("invalid parameters");
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) { throw new IllegalArgumentException("user not found"); }
        Optional<StudyLog> existing = studyLogRepository.findByUserIdAndStudyDate(userId, studyDate);
        if (existing.isPresent()) {
            StudyLog log = existing.get();
            log.setMinutesStudied((log.getMinutesStudied() == null ? 0 : log.getMinutesStudied()) + minutesStudied);
            if (notes != null && !notes.isBlank()) {
                String current = log.getNotes();
                log.setNotes(current == null ? notes : current + "\n" + notes);
            }
            return studyLogRepository.save(log);
        } else {
            StudyLog newLog = new StudyLog(userOpt.get(), studyDate, minutesStudied, notes);
            return studyLogRepository.save(newLog);
        }
    }

    public List<WeeklyProgressDto> getWeeklyProgress(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SATURDAY));
        List<WeeklyProgressDto> weekly = new ArrayList<>();
        List<LearningGoal> active = learningGoalRepository.findActiveGoalsByUserId(userId);
        int target = active != null ? active.stream().mapToInt(g -> g.getDailyTargetMinutes() == null ? 0 : g.getDailyTargetMinutes()).sum() : 0;
        for (LocalDate d = weekStart; !d.isAfter(weekEnd); d = d.plusDays(1)) {
            Integer minutes = studyLogRepository.sumMinutesByUserIdAndStudyDate(userId, d);
            if (minutes == null) minutes = 0;
            weekly.add(new WeeklyProgressDto(d, minutes, target));
        }
        return weekly;
    }

    public Integer getTotalStudyMinutes(Long userId, LocalDate startDate, LocalDate endDate) {
        List<StudyLog> logs = studyLogRepository.findByUserIdAndStudyDateBetween(userId, startDate, endDate);
        return logs == null ? 0 : logs.stream().mapToInt(l -> l.getMinutesStudied() == null ? 0 : l.getMinutesStudied()).sum();
    }

    public void deleteById(Long id) { studyLogRepository.deleteById(id); }

    public long count() { return studyLogRepository.count(); }
}

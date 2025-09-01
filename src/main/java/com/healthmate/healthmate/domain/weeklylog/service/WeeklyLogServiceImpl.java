package com.healthmate.healthmate.domain.weeklylog.service;

import com.healthmate.healthmate.domain.user.entity.User;
import com.healthmate.healthmate.domain.user.repository.UserRepository;
import com.healthmate.healthmate.domain.weeklylog.dto.WeeklyLogDtos.UpsertCurrentWeekRequest;
import com.healthmate.healthmate.domain.weeklylog.dto.WeeklyLogDtos.WeeklyLogResponse;
import com.healthmate.healthmate.domain.weeklylog.entity.WeeklyLog;
import com.healthmate.healthmate.domain.weeklylog.repository.WeeklyLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeeklyLogServiceImpl implements WeeklyLogService {

    private final WeeklyLogRepository weeklyLogRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public WeeklyLogResponse upsertCurrentWeek(Long userId, UpsertCurrentWeekRequest req) {
        LocalDate today = LocalDate.now();
        WeekFields wf = WeekFields.ISO;
        int year = today.get(wf.weekBasedYear());
        int week = today.get(wf.weekOfWeekBasedYear());

        WeeklyLog log = weeklyLogRepository.findByUserIdAndYearAndWeek(userId, year, week)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));
                    return new WeeklyLog(user, year, week, 0, 0.0, 0.0, 0);
                });

        log.update(req.weeklyExerciseMinutes(), req.weeklyAvgWeight(), req.weeklyAvgBmi(), req.caloriesBurned());
        WeeklyLog saved = weeklyLogRepository.save(log);
        return new WeeklyLogResponse(saved.getId(), saved.getYear(), saved.getWeek(), saved.getExerciseMinutes(), saved.getAvgWeight(), saved.getAvgBmi(), saved.getCaloriesBurned());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WeeklyLogResponse> getMyLogs(Long userId) {
        return weeklyLogRepository.findAllByUserIdOrderByYearAscWeekAsc(userId)
                .stream()
                .map(l -> new WeeklyLogResponse(l.getId(), l.getYear(), l.getWeek(), l.getExerciseMinutes(), l.getAvgWeight(), l.getAvgBmi(), l.getCaloriesBurned()))
                .toList();
    }
}



package com.healthmate.healthmate.domain.weeklylog.repository;

import com.healthmate.healthmate.domain.weeklylog.entity.WeeklyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WeeklyLogRepository extends JpaRepository<WeeklyLog, Long> {
    Optional<WeeklyLog> findByUserIdAndYearAndWeek(Long userId, int year, int week);
    List<WeeklyLog> findAllByUserIdOrderByYearAscWeekAsc(Long userId);
}



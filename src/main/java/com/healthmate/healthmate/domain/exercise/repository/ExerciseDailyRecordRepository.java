package com.healthmate.healthmate.domain.exercise.repository;

import com.healthmate.healthmate.domain.exercise.entity.ExerciseDailyRecord;
import com.healthmate.healthmate.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExerciseDailyRecordRepository extends JpaRepository<ExerciseDailyRecord, Long> {
    Optional<ExerciseDailyRecord> findByUserAndDate(User user, LocalDate date);
    List<ExerciseDailyRecord> findByUserOrderByDateDesc(User user);
}



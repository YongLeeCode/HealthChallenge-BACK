package com.healthmate.healthmate.domain.exercise.service;

import com.healthmate.healthmate.domain.exercise.dto.request.AddExerciseDailyRecordRequestDto;
import com.healthmate.healthmate.domain.exercise.dto.request.UpdateExerciseDailyRecordRequestDto;
import com.healthmate.healthmate.domain.exercise.dto.response.ExerciseDailyRecordResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface ExerciseDailyRecordService {
    Long upsert(AddExerciseDailyRecordRequestDto req);
    void update(Long id, UpdateExerciseDailyRecordRequestDto req);
    void delete(Long id);
    ExerciseDailyRecordResponseDto get(Long id);
    ExerciseDailyRecordResponseDto getByUserAndDate(Long userId, LocalDate date);
    List<ExerciseDailyRecordResponseDto> listByUser(Long userId);
}



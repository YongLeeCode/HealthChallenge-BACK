package com.healthmate.healthmate.domain.exercise.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ExerciseDailyRecordResponseDto {
    private Long id;
    private Long userId;
    private LocalDate date;
    private Integer totalDurationSeconds;
    private Integer totalSets;
    private Integer perceivedDifficulty;
    private Integer satisfaction;
    private Long representativeExerciseId;
    private String notes;
}



package com.healthmate.healthmate.domain.exercise.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AddExerciseDailyRecordRequestDto {
    private Long userId;
    private LocalDate date;
    private Integer totalDurationSeconds;
    private Integer totalSets;
    private Integer perceivedDifficulty;
    private Integer satisfaction;
    private Long representativeExerciseId;
    private String notes;
}



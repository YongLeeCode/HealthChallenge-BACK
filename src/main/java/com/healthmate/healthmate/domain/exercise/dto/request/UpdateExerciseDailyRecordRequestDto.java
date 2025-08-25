package com.healthmate.healthmate.domain.exercise.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateExerciseDailyRecordRequestDto {
    private Integer totalDurationSeconds;
    private Integer totalSets;
    private Integer perceivedDifficulty;
    private Integer satisfaction;
    private Long representativeExerciseId;
    private String notes;
}



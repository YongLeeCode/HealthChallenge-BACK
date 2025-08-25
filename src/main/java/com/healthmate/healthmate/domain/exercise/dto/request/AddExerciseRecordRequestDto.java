package com.healthmate.healthmate.domain.exercise.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AddExerciseRecordRequestDto {
    private Long userId;
    private Long exerciseId;
    private LocalDateTime performedAt;
    private Integer durationSeconds;
    private Integer reps;
    private Integer sets;
    private String notes;
}



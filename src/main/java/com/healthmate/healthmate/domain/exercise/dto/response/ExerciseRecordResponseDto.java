package com.healthmate.healthmate.domain.exercise.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ExerciseRecordResponseDto {
    private Long id;
    private Long userId;
    private Long exerciseId;
    private LocalDateTime performedAt;
    private Integer durationSeconds;
    private Integer reps;
    private Integer sets;
    private String notes;
}



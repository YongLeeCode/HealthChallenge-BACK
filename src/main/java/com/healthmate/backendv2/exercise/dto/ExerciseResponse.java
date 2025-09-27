package com.healthmate.backendv2.exercise.dto;

import com.healthmate.backendv2.exercise.MeasurementType;
import com.healthmate.backendv2.exercise.MuscleFocusArea;
import com.healthmate.backendv2.exercise.ExerciseType;
import com.healthmate.backendv2.exercise.entity.Exercise;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class ExerciseResponse {
    @NotNull Long id;
    String name;
    String description;
    MeasurementType measurementType;
    MuscleFocusArea muscleFocusArea;
    ExerciseType exerciseType;
    String imageUrl;

    public static ExerciseResponse from(Exercise exercise) {
        return ExerciseResponse.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .description(exercise.getDescription())
                .measurementType(exercise.getMeasurementType())
                .muscleFocusArea(exercise.getMuscleFocusArea())
                .exerciseType(exercise.getExerciseType())
                .imageUrl(exercise.getImageUrl())
                .build();
    }
}

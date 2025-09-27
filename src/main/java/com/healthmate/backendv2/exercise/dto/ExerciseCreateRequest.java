package com.healthmate.backendv2.exercise.dto;

import com.healthmate.backendv2.exercise.MeasurementType;
import com.healthmate.backendv2.exercise.MuscleFocusArea;
import com.healthmate.backendv2.exercise.ExerciseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ExerciseCreateRequest {
    @NotBlank String name;
    String description;
    @NotNull MeasurementType measurementType;
    @NotNull MuscleFocusArea muscleFocusArea;
    @NotNull ExerciseType exerciseType;
    String imageUrl;
}

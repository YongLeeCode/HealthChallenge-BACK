package com.healthmate.backendv2.challenge.dto;

import com.healthmate.backendv2.challenge.entity.ChallengeTemplate;
import com.healthmate.backendv2.exercise.dto.ExerciseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeTemplateResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Boolean isActive;
    private LocalDate createdAt;
    private List<ExerciseTemplateResponse> exercises;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExerciseTemplateResponse {
        private Long id;
        private Long exerciseId;
        private String exerciseName;
        private String exerciseDescription;
        private String measurementType;
        private String muscleFocusArea;
        private String exerciseType;
        private String imageUrl;
        private Integer targetSets;
        private Integer targetDurationMinutes;
        private Integer pointsPerSet;
        private Integer pointsPerMinute;
        private Boolean isRequired;
        private Integer orderIndex;
    }

    public static ChallengeTemplateResponse from(ChallengeTemplate template) {
        return ChallengeTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .startDate(template.getStartDate())
                .endDate(template.getEndDate())
                .status(template.getStatus().name())
                .isActive(template.isActive())
                .createdAt(template.getCreatedAt())
                .exercises(template.getExercises().stream()
                        .map(exercise -> ExerciseTemplateResponse.from(exercise, null))
                        .toList())
                .build();
    }

    public static ChallengeTemplateResponse from(ChallengeTemplate template, List<ExerciseResponse> exerciseResponses) {
        return ChallengeTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .startDate(template.getStartDate())
                .endDate(template.getEndDate())
                .status(template.getStatus().name())
                .isActive(template.isActive())
                .createdAt(template.getCreatedAt())
                .exercises(template.getExercises().stream()
                        .map(exercise -> {
                            ExerciseResponse exerciseResponse = exerciseResponses.stream()
                                    .filter(er -> er.getId().equals(exercise.getExerciseId()))
                                    .findFirst()
                                    .orElse(null);
                            return ExerciseTemplateResponse.from(exercise, exerciseResponse);
                        })
                        .toList())
                .build();
    }

    public static ExerciseTemplateResponse from(ChallengeTemplate.ChallengeTemplateExercise exercise, ExerciseResponse exerciseResponse) {
        ExerciseTemplateResponse.ExerciseTemplateResponseBuilder builder = ExerciseTemplateResponse.builder()
                .id(exercise.getId())
                .exerciseId(exercise.getExerciseId())
                .targetSets(exercise.getTargetSets())
                .targetDurationMinutes(exercise.getTargetDurationMinutes())
                .pointsPerSet(exercise.getPointsPerSet())
                .pointsPerMinute(exercise.getPointsPerMinute())
                .isRequired(exercise.isRequired())
                .orderIndex(exercise.getOrderIndex());

        if (exerciseResponse != null) {
            builder.exerciseName(exerciseResponse.getName())
                    .exerciseDescription(exerciseResponse.getDescription())
                    .measurementType(exerciseResponse.getMeasurementType() != null ? exerciseResponse.getMeasurementType().name() : null)
                    .muscleFocusArea(exerciseResponse.getMuscleFocusArea() != null ? exerciseResponse.getMuscleFocusArea().name() : null)
                    .exerciseType(exerciseResponse.getExerciseType() != null ? exerciseResponse.getExerciseType().name() : null)
                    .imageUrl(exerciseResponse.getImageUrl());
        }

        return builder.build();
    }
}

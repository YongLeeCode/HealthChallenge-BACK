package com.healthmate.backendv2.challenge.dto;

import com.healthmate.backendv2.challenge.entity.ChallengeTemplate;
import com.healthmate.backendv2.challenge.entity.ChallengeTemplateExercise;
import com.healthmate.backendv2.challenge.entity.TimeAttackTemplateExercise;
import com.healthmate.backendv2.challenge.entity.WeightTemplateExercise;
import com.healthmate.backendv2.challenge.entity.WorkingTimeTemplateExercise;
import com.healthmate.backendv2.exercise.dto.ExerciseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
    private LocalDate createdAt;
    private List<ExerciseTemplateResponse> exercises;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
	@SuperBuilder
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
    )
    @JsonSubTypes({
        @JsonSubTypes.Type(value = TimeAttackExerciseTemplateResponse.class, name = "TIME_ATTACK"),
        @JsonSubTypes.Type(value = WeightExerciseTemplateResponse.class, name = "WEIGHT"),
        @JsonSubTypes.Type(value = WorkingTimeExerciseTemplateResponse.class, name = "WORKING_TIME")
    })
    public static abstract class ExerciseTemplateResponse {
        private Long id;
        private Long exerciseId;
        private String exerciseName;
        private String exerciseDescription;
        private String measurementType;
        private String muscleFocusArea;
        private String exerciseType;
        private String imageUrl;
        private Integer orderIndex;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public static class TimeAttackExerciseTemplateResponse extends ExerciseTemplateResponse {
        private Integer pointsPerSecond;
        private Integer maxPoints;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public static class WeightExerciseTemplateResponse extends ExerciseTemplateResponse {
        private Integer pointsPerWeight;
		private Integer pointsPerCount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public static class WorkingTimeExerciseTemplateResponse extends ExerciseTemplateResponse {
		private Integer pointsPerSecond;
    }

    public static ChallengeTemplateResponse from(ChallengeTemplate template, List<ExerciseResponse> exerciseResponses) {
        return ChallengeTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .startDate(template.getStartDate())
                .endDate(template.getEndDate())
                .createdAt(template.getCreatedAt())
                .exercises(template.getExercises().stream()
                        .map(exercise -> {
                            ExerciseResponse exerciseResponse = exerciseResponses.stream()
                                    .filter(er -> er.getId().equals(exercise.getExerciseId()))
                                    .findFirst()
                                    .orElse(null);
                            return fromExerciseTemplate(exercise, exerciseResponse);
                        })
                        .toList())
                .build();
    }

    /**
     * 타입별 엔티티를 적절한 Response DTO로 변환
     */
    public static ExerciseTemplateResponse fromExerciseTemplate(ChallengeTemplateExercise exercise, ExerciseResponse exerciseResponse) {
        // 타입별로 적절한 Response 생성
        if (exercise instanceof TimeAttackTemplateExercise timeAttackExercise) {
            return TimeAttackExerciseTemplateResponse.builder()
                    .id(timeAttackExercise.getId())
                    .exerciseId(timeAttackExercise.getExerciseId())
                    .exerciseName(exerciseResponse != null ? exerciseResponse.getName() : null)
                    .exerciseDescription(exerciseResponse != null ? exerciseResponse.getDescription() : null)
                    .measurementType(exerciseResponse != null && exerciseResponse.getMeasurementType() != null ? exerciseResponse.getMeasurementType().name() : null)
                    .muscleFocusArea(exerciseResponse != null && exerciseResponse.getMuscleFocusArea() != null ? exerciseResponse.getMuscleFocusArea().name() : null)
                    .exerciseType(exerciseResponse != null && exerciseResponse.getExerciseType() != null ? exerciseResponse.getExerciseType().name() : null)
                    .imageUrl(exerciseResponse != null ? exerciseResponse.getImageUrl() : null)
                    .orderIndex(timeAttackExercise.getOrderIndex())
                    .pointsPerSecond(timeAttackExercise.getPointsPerSecond())
                    .maxPoints(timeAttackExercise.getMaxPoints())
                    .build();
        } else if (exercise instanceof WeightTemplateExercise weightExercise) {
            return WeightExerciseTemplateResponse.builder()
                    .id(weightExercise.getId())
                    .exerciseId(weightExercise.getExerciseId())
                    .exerciseName(exerciseResponse != null ? exerciseResponse.getName() : null)
                    .exerciseDescription(exerciseResponse != null ? exerciseResponse.getDescription() : null)
                    .measurementType(exerciseResponse != null && exerciseResponse.getMeasurementType() != null ? exerciseResponse.getMeasurementType().name() : null)
                    .muscleFocusArea(exerciseResponse != null && exerciseResponse.getMuscleFocusArea() != null ? exerciseResponse.getMuscleFocusArea().name() : null)
                    .exerciseType(exerciseResponse != null && exerciseResponse.getExerciseType() != null ? exerciseResponse.getExerciseType().name() : null)
                    .imageUrl(exerciseResponse != null ? exerciseResponse.getImageUrl() : null)
                    .orderIndex(weightExercise.getOrderIndex())
                    .pointsPerWeight(weightExercise.getPointsPerWeight())
                    .pointsPerCount(weightExercise.getPointsPerCount())
                    .build();
        } else if (exercise instanceof WorkingTimeTemplateExercise workingTimeExercise) {
            return WorkingTimeExerciseTemplateResponse.builder()
                    .id(workingTimeExercise.getId())
                    .exerciseId(workingTimeExercise.getExerciseId())
                    .exerciseName(exerciseResponse != null ? exerciseResponse.getName() : null)
                    .exerciseDescription(exerciseResponse != null ? exerciseResponse.getDescription() : null)
                    .measurementType(exerciseResponse != null && exerciseResponse.getMeasurementType() != null ? exerciseResponse.getMeasurementType().name() : null)
                    .muscleFocusArea(exerciseResponse != null && exerciseResponse.getMuscleFocusArea() != null ? exerciseResponse.getMuscleFocusArea().name() : null)
                    .exerciseType(exerciseResponse != null && exerciseResponse.getExerciseType() != null ? exerciseResponse.getExerciseType().name() : null)
                    .imageUrl(exerciseResponse != null ? exerciseResponse.getImageUrl() : null)
                    .orderIndex(workingTimeExercise.getOrderIndex())
                    .pointsPerSecond(workingTimeExercise.getPointsPerSecond())
                    .build();
        }

        throw new IllegalArgumentException("지원하지 않는 운동 템플릿 타입입니다: " + exercise.getClass().getSimpleName());
    }
}

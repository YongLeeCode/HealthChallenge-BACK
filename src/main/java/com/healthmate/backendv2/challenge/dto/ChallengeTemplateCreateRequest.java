package com.healthmate.backendv2.challenge.dto;

import com.healthmate.backendv2.challenge.entity.ChallengeTemplate;
import com.healthmate.backendv2.exercise.MeasurementType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeTemplateCreateRequest {

    @NotBlank(message = "챌린지 이름은 필수입니다")
    private String name;

    private String description;

    @NotNull(message = "시작일은 필수입니다")
    private LocalDate startDate;

    @NotNull(message = "종료일은 필수입니다")
    private LocalDate endDate;

    @NotEmpty(message = "운동 목록은 비어있을 수 없습니다")
    @Valid
    private List<ExerciseTemplateRequest> exercises;

    @Getter
    @NoArgsConstructor
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
    )
    @JsonSubTypes({
        @JsonSubTypes.Type(value = TimeAttackExerciseTemplateRequest.class, name = "TIME_ATTACK"),
        @JsonSubTypes.Type(value = WeightExerciseTemplateRequest.class, name = "WEIGHT"),
        @JsonSubTypes.Type(value = WorkingTimeExerciseTemplateRequest.class, name = "WORKING_TIME")
    })
    public static abstract class ExerciseTemplateRequest {
        @NotNull(message = "운동 ID는 필수입니다")
        private Long exerciseId;
        
        @NotNull(message = "측정 타입은 필수입니다")
        private MeasurementType type;

        private Integer orderIndex;
        
        // Jackson이 필드를 설정할 수 있도록 setter 추가
        public void setExerciseId(Long exerciseId) {
            this.exerciseId = exerciseId;
        }
        
        public void setType(MeasurementType type) {
            this.type = type;
        }
        
        public void setOrderIndex(Integer orderIndex) {
            this.orderIndex = orderIndex;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class TimeAttackExerciseTemplateRequest extends ExerciseTemplateRequest {
        @Positive(message = "초당 점수는 양수여야 합니다")
        private Integer pointsPerSecond;

        @Positive(message = "최대 점수는 양수입니다.")
        private Integer maxPoints;
        
        // Jackson이 필드를 설정할 수 있도록 setter 추가
        public void setPointsPerSecond(Integer pointsPerSecond) {
            this.pointsPerSecond = pointsPerSecond;
        }
        
        public void setMaxPoints(Integer maxPoints) {
            this.maxPoints = maxPoints;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class WeightExerciseTemplateRequest extends ExerciseTemplateRequest {
        @Positive(message = "무게(kg)당 점수는 양수여야 합니다")
        private Integer pointsPerWeight;

        @Positive(message = "횟수당 점수는 양수여야 합니다")
        private Integer pointsPerCount;
        
        // Jackson이 필드를 설정할 수 있도록 setter 추가
        public void setPointsPerWeight(Integer pointsPerWeight) {
            this.pointsPerWeight = pointsPerWeight;
        }
        
        public void setPointsPerCount(Integer pointsPerCount) {
            this.pointsPerCount = pointsPerCount;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class WorkingTimeExerciseTemplateRequest extends ExerciseTemplateRequest {
        @Positive(message = "초당 점수는 양수여야 합니다")
        private Integer pointsPerSecond;
        
        // Jackson이 필드를 설정할 수 있도록 setter 추가
        public void setPointsPerSecond(Integer pointsPerSecond) {
            this.pointsPerSecond = pointsPerSecond;
        }
    }
}

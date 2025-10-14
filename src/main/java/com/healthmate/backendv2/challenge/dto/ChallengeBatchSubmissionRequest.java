package com.healthmate.backendv2.challenge.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.healthmate.backendv2.exercise.MeasurementType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeBatchSubmissionRequest {
    
    @NotNull(message = "챌린지 ID는 필수입니다")
    private String challengeId;
    
    @NotEmpty(message = "운동 목록은 비어있을 수 없습니다")
    @Valid
    private List<ExerciseSubmission> exercises;

    private String notes;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
	@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "type"
	)
	@JsonSubTypes({
		@JsonSubTypes.Type(value = TimeAttackExerciseSubmission.class, name = "TIME_ATTACK"),
		@JsonSubTypes.Type(value = WorkingTimeExerciseSubmission.class, name = "WORKING_TIME"),
		@JsonSubTypes.Type(value = WeightExerciseSubmission.class, name = "WEIGHT")
	})
	public static abstract class ExerciseSubmission{
		@NotNull(message = "운동 ID는 필수입니다")
		private Long exerciseId;
		private MeasurementType type;
		private String exerciseNotes;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
    public static class TimeAttackExerciseSubmission extends ExerciseSubmission{
        private Integer completionTimeSeconds;
    }

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class WorkingTimeExerciseSubmission extends ExerciseSubmission{
		private Integer durationTimeSeconds;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class WeightExerciseSubmission extends ExerciseSubmission{
		private Double maxWeightKg;
		private Integer counts;
	}
}

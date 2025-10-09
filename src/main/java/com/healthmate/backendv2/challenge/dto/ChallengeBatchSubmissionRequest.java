package com.healthmate.backendv2.challenge.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
    public static class ExerciseSubmission {
        @NotNull(message = "운동 ID는 필수입니다")
        private Long exerciseId;
        
        // WORKING_TIME용 필드: exerciseId, durationTimeSeconds
        private Integer durationTimeSeconds;
        
        // WEIGHT용 필드: exerciseId, maxWeightKg, counts
        private Double maxWeightKg;
        private Integer counts;
        
        // TIME_ATTACK용 필드: exerciseId, completionTimeSeconds
        private Integer completionTimeSeconds;
        
        private String exerciseNotes;
    }
}

package com.healthmate.backendv2.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeBatchSubmissionResponse {
    
    private String challengeId;
    private Integer totalPointsEarned;
    private Integer totalPoints;
    private Integer currentRank;
    private LocalDateTime submittedAt;
    private List<ExerciseSubmissionResult> exerciseResults;
    private String notes;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExerciseSubmissionResult {
        private Long exerciseId;
        private String exerciseName;
        private String exerciseType;
        private Integer pointsEarned;
        private String status;
        private String errorMessage;
    }
}

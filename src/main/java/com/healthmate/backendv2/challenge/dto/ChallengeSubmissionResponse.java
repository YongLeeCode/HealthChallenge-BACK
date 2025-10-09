package com.healthmate.backendv2.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeSubmissionResponse {
    
    private Long submissionId;
    private Long exerciseId;
    private String exerciseName;
    private Integer sets;
    private Integer durationMinutes;
    private Integer pointsEarned;
    private Integer totalPoints;
    private Integer currentRank;
    private LocalDateTime submittedAt;
    private String notes;
}

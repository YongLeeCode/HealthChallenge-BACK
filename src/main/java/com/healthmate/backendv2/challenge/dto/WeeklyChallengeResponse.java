package com.healthmate.backendv2.challenge.dto;

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
public class WeeklyChallengeResponse {
    
    private String challengeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // ACTIVE, COMPLETED, UPCOMING
    private Integer currentUserPoints;
    private Integer currentUserRank;
    private List<ChallengeExercise> exercises;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChallengeExercise {
        private Long exerciseId;
        private String exerciseName;
        private String description;
        private String measurementType;
        private String muscleFocusArea;
        private String exerciseType;
        private String imageUrl;
        private Integer targetSets;
        private Integer targetDurationMinutes;
        private Integer pointsPerSet;
        private Integer pointsPerMinute;
    }
}

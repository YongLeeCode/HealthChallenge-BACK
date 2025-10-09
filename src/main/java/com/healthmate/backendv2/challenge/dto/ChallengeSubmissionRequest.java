package com.healthmate.backendv2.challenge.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeSubmissionRequest {
    
    @NotNull(message = "운동 ID는 필수입니다")
    private Long exerciseId;
    
    @NotNull(message = "세트 수는 필수입니다")
    @Positive(message = "세트 수는 양수여야 합니다")
    private Integer sets;
    
    @NotNull(message = "운동 시간은 필수입니다")
    @Positive(message = "운동 시간은 양수여야 합니다")
    private Integer durationMinutes;
    
    private String notes;
}

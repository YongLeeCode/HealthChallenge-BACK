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
public class TimeAttackSubmissionRequest {
    
    @NotNull(message = "운동 ID는 필수입니다")
    private Long exerciseId;
    
    @NotNull(message = "완료 시간(초)은 필수입니다")
    @Positive(message = "완료 시간은 양수여야 합니다")
    private Integer completionTimeSeconds;
    
    @NotNull(message = "목표 횟수는 필수입니다")
    @Positive(message = "목표 횟수는 양수여야 합니다")
    private Integer targetCount;
    
    private String notes;
}

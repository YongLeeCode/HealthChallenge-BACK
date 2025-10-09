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
public class WorkingTimeSubmissionRequest {
    
    @NotNull(message = "운동 ID는 필수입니다")
    private Long exerciseId;
    
    @NotNull(message = "지속 시간(초)은 필수입니다")
    @Positive(message = "지속 시간은 양수여야 합니다")
    private Integer durationTimeSeconds;
    
    private String notes;
}

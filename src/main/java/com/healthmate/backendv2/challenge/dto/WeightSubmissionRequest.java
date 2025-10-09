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
public class WeightSubmissionRequest {
    
    @NotNull(message = "운동 ID는 필수입니다")
    private Long exerciseId;
    
    @NotNull(message = "최대 무게(kg)는 필수입니다")
    @Positive(message = "무게는 양수여야 합니다")
    private Double maxWeightKg;
    
    @NotNull(message = "횟수는 필수입니다")
    @Positive(message = "횟수는 양수여야 합니다")
    private Integer counts;
    
    private String notes;
}

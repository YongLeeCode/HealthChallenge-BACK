package com.healthmate.backendv2.challenge.dto;

import com.healthmate.backendv2.challenge.entity.ChallengeTemplate;
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
    @AllArgsConstructor
    @Builder
    public static class ExerciseTemplateRequest {
        @NotNull(message = "운동 ID는 필수입니다")
        private Long exerciseId;

        @Positive(message = "목표 세트 수는 양수여야 합니다")
        private Integer targetSets;

        @Positive(message = "목표 지속시간은 양수여야 합니다")
        private Integer targetDurationMinutes;

        @Positive(message = "세트당 점수는 양수여야 합니다")
        private Integer pointsPerSet;

        @Positive(message = "분당 점수는 양수여야 합니다")
        private Integer pointsPerMinute;

        @Builder.Default
        private Boolean isRequired = true;

        @Builder.Default
        private Integer orderIndex = 0;
    }
}

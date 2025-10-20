package com.healthmate.backendv2.challenge.dto;

import com.healthmate.backendv2.challenge.entity.ChallengeTemplate;
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
public class ChallengeTemplateResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate createdAt;
    private List<Long> exerciseUnitIds;

    public static ChallengeTemplateResponse from(ChallengeTemplate template) {
        return ChallengeTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .startDate(template.getStartDate())
                .endDate(template.getEndDate())
                .createdAt(template.getCreatedAt())
                .exerciseUnitIds(template.getExerciseUnitIds())
                .build();
    }
}

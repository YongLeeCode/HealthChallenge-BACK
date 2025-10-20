package com.healthmate.backendv2.challenge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "challenge_templates")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    @NotNull
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    @NotNull
    private LocalDate endDate;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDate createdAt;

    @ElementCollection
    @CollectionTable(name = "challenge_template_unit_ids", joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "unit_id", nullable = false)
    @OrderColumn(name = "order_index")
    @Builder.Default
    private List<Long> exerciseUnitIds = new ArrayList<>();
    // 편의 메서드
    public void addExerciseUnitId(Long unitId) {
        exerciseUnitIds.add(unitId);
    }
}

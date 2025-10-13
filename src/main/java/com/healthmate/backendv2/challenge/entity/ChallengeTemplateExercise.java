package com.healthmate.backendv2.challenge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "challenge_template_exercises")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeTemplateExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_template_id", nullable = false)
    @NotNull
    private ChallengeTemplate challengeTemplate;

    @Column(name = "exercise_id", nullable = false)
    @NotNull
    private Long exerciseId;

    @Column(name = "target_sets")
    @Positive
    private Integer targetSets;

    @Column(name = "target_duration_minutes")
    @Positive
    private Integer targetDurationMinutes;

    @Column(name = "points_per_set")
    @Positive
    private Integer pointsPerSet;

    @Column(name = "points_per_minute")
    @Positive
    private Integer pointsPerMinute;

    @Column(name = "is_required", nullable = false)
    @Builder.Default
    private Boolean isRequired = true;

    @Column(name = "order_index", nullable = false)
    @Builder.Default
    private Integer orderIndex = 0;

    // 편의 메서드
    public void setChallengeTemplate(ChallengeTemplate challengeTemplate) {
        this.challengeTemplate = challengeTemplate;
    }

    public boolean isRequired() {
        return isRequired != null && isRequired;
    }

    public boolean hasTargetSets() {
        return targetSets != null && targetSets > 0;
    }

    public boolean hasTargetDuration() {
        return targetDurationMinutes != null && targetDurationMinutes > 0;
    }

    public boolean hasPointsPerSet() {
        return pointsPerSet != null && pointsPerSet > 0;
    }

    public boolean hasPointsPerMinute() {
        return pointsPerMinute != null && pointsPerMinute > 0;
    }
}

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
    private LocalDate createdAt = LocalDate.now();

    @OneToMany(mappedBy = "challengeTemplate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ChallengeTemplateExercise> exercises = new ArrayList<>();

    // 편의 메서드
    public void addExercise(ChallengeTemplateExercise exercise) {
        exercises.add(exercise);
        exercise.setChallengeTemplate(this);
    }

    public void removeExercise(ChallengeTemplateExercise exercise) {
        exercises.remove(exercise);
        exercise.setChallengeTemplate(null);
    }

    // 타입별 운동 조회 메서드들
    public List<TimeAttackTemplateExercise> getTimeAttackExercises() {
        return exercises.stream()
                .filter(ex -> ex instanceof TimeAttackTemplateExercise)
                .map(ex -> (TimeAttackTemplateExercise) ex)
                .toList();
    }

    public List<WeightTemplateExercise> getWeightExercises() {
        return exercises.stream()
                .filter(ex -> ex instanceof WeightTemplateExercise)
                .map(ex -> (WeightTemplateExercise) ex)
                .toList();
    }

    public List<WorkingTimeTemplateExercise> getWorkingTimeExercises() {
        return exercises.stream()
                .filter(ex -> ex instanceof WorkingTimeTemplateExercise)
                .map(ex -> (WorkingTimeTemplateExercise) ex)
                .toList();
    }
}

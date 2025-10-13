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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull
    private ChallengeStatus status;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDate createdAt = LocalDate.now();

    @OneToMany(mappedBy = "challengeTemplate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ChallengeTemplateExercise> exercises = new ArrayList<>();

    public enum ChallengeStatus {
        UPCOMING("예정"),
        ACTIVE("진행중"),
        COMPLETED("완료");

        private final String description;

        ChallengeStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // 편의 메서드
    public void addExercise(ChallengeTemplateExercise exercise) {
        exercises.add(exercise);
        exercise.setChallengeTemplate(this);
    }

    public void removeExercise(ChallengeTemplateExercise exercise) {
        exercises.remove(exercise);
        exercise.setChallengeTemplate(null);
    }

    public boolean isActive() {
        return isActive != null && isActive;
    }

    public boolean isUpcoming() {
        return status == ChallengeStatus.UPCOMING;
    }

    public boolean isCompleted() {
        return status == ChallengeStatus.COMPLETED;
    }
}

package com.healthmate.backendv2.challenge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import com.healthmate.backendv2.exercise.MeasurementType;

@Entity
@Table(name = "challenge_template_exercises")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "exercise_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class ChallengeTemplateExercise {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_type", nullable = false)
    @NotNull
    private MeasurementType measurementType;

    @Column(name = "is_required", nullable = false)
    @Builder.Default
    private Boolean isRequired = true;

    @Column(name = "order_index", nullable = false)
    @Builder.Default
    private Integer orderIndex = 0;

    // 각 타입별로 구현해야 하는 추상 메서드
    public abstract String getTypeSpecificDescription();
}
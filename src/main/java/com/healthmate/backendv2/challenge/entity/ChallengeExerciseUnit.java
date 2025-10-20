package com.healthmate.backendv2.challenge.entity;

import com.healthmate.backendv2.exercise.MeasurementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "challenge_exercise_units")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "exercise_type")
@Getter
@NoArgsConstructor
@SuperBuilder
public abstract class ChallengeExerciseUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exercise_id", nullable = false)
    @NotNull
    private Long exerciseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_type", nullable = false)
    @NotNull
    protected MeasurementType measurementType;
}



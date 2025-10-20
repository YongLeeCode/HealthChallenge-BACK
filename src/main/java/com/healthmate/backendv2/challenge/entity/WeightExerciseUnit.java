package com.healthmate.backendv2.challenge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("WEIGHT")
@Getter
@NoArgsConstructor
@SuperBuilder
public class WeightExerciseUnit extends ChallengeExerciseUnit {

    @Column(name = "points_per_weight")
    @Positive
    private Integer pointsPerWeight;

    @Column(name = "points_per_count")
    @Positive
    private Integer pointsPerCount;
}



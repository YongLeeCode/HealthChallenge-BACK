package com.healthmate.backendv2.challenge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("WORKING_TIME")
@Getter
@NoArgsConstructor
@SuperBuilder
public class WorkingTimeExerciseUnit extends ChallengeExerciseUnit {

    @Column(name = "points_per_second")
    @Positive
    private Integer pointsPerSecond;
}



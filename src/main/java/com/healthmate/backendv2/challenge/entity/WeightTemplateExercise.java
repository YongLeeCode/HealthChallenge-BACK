package com.healthmate.backendv2.challenge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import com.healthmate.backendv2.exercise.MeasurementType;

@Entity
@DiscriminatorValue("WEIGHT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WeightTemplateExercise extends ChallengeTemplateExercise {

    @Column(name = "target_sets")
    @Positive
    private Integer targetSets;

    @Column(name = "target_weight_kg")
    @Positive
    private Double targetWeightKg;

    @Column(name = "points_per_set")
    @Positive
    private Integer pointsPerSet;

    @Column(name = "points_per_kg")
    @Positive
    private Integer pointsPerKg;

    @Column(name = "heavy_weight_threshold_kg")
    @Positive
    private Double heavyWeightThresholdKg;

    @Column(name = "heavy_weight_multiplier")
    @Positive
    private Double heavyWeightMultiplier;

    public WeightTemplateExercise() {
        super();
        this.measurementType = MeasurementType.WEIGHT;
    }

    @Override
    public String getTypeSpecificDescription() {
        return String.format("목표 세트: %d세트, 목표 무게: %.1fkg, 세트당 점수: %d점", 
            targetSets, targetWeightKg, pointsPerSet);
    }
}

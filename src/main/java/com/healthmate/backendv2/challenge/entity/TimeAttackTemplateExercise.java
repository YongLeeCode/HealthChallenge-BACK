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
@DiscriminatorValue("TIME_ATTACK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TimeAttackTemplateExercise extends ChallengeTemplateExercise {

    @Column(name = "target_completion_time_seconds")
    @Positive
    private Integer targetCompletionTimeSeconds;

    @Column(name = "points_per_second")
    @Positive
    private Integer pointsPerSecond;

    @Column(name = "fast_completion_bonus_threshold")
    @Positive
    private Integer fastCompletionBonusThreshold;

    @Column(name = "fast_completion_bonus_points")
    @Positive
    private Integer fastCompletionBonusPoints;

    public TimeAttackTemplateExercise() {
        super();
        this.measurementType = MeasurementType.TIME_ATTACK;
    }

    @Override
    public String getTypeSpecificDescription() {
        return String.format("목표 완료시간: %d초, 초당 점수: %d점", 
            targetCompletionTimeSeconds, pointsPerSecond);
    }
}

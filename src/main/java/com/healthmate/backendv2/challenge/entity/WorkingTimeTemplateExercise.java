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
@DiscriminatorValue("WORKING_TIME")
@Getter
@NoArgsConstructor
@SuperBuilder
public class WorkingTimeTemplateExercise extends ChallengeTemplateExercise {

    @Column(name = "points_per_second")
    @Positive
    private Integer pointsPerSecond;

    public WorkingTimeTemplateExercise(Integer pointsPerSecond) {
        super();
		this.pointsPerSecond = pointsPerSecond;
        this.measurementType = MeasurementType.WORKING_TIME;
    }

    @Override
    public String getTypeSpecificDescription() {
        return String.format("점수 측정 방법: 시간(seconds) * %d",
            pointsPerSecond);
    }
}

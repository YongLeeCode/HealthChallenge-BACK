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
@SuperBuilder
@NoArgsConstructor
public class WeightTemplateExercise extends ChallengeTemplateExercise {

    @Column(name = "points_per_weight")
    @Positive
    private Integer pointsPerWeight;

	@Column(name = "points_per_count")
	@Positive
	private Integer pointsPerCount;

    public WeightTemplateExercise(Integer pointsPerWeight, Integer pointsPerCount) {
        super();
		this.pointsPerWeight = pointsPerWeight;
		this.pointsPerCount = pointsPerCount;
        this.measurementType = MeasurementType.WEIGHT;
    }

    @Override
    public String getTypeSpecificDescription() {
        return String.format("점수 측정 방법: 무게 점수(사용 기구 무게 x %d) + (성공 횟수 x %d)",
            pointsPerWeight, pointsPerCount);
    }
}

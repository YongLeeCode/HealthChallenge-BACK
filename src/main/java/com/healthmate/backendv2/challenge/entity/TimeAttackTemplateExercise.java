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
@NoArgsConstructor
@SuperBuilder
public class TimeAttackTemplateExercise extends ChallengeTemplateExercise {

    @Column(name = "points_per_second")
    @Positive
    private Integer pointsPerSecond;

	@Column(name = "max_points")
	@Positive
	private Integer maxPoints;


    @Override
    public String getTypeSpecificDescription() {
        return String.format("점수 측정 방법: 최고 점수(%d) - (소모한 시간 x %d)",
			pointsPerSecond, maxPoints);
    }
}

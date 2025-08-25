package com.healthmate.healthmate.domain.exercise.dto.response;

import com.healthmate.healthmate.domain.exercise.entity.Exercise;
import com.healthmate.healthmate.domain.exercise.enums.ExerciseDifficulty;

public record ExerciseResponseDto(
	Long id,
	String nameEn,
	String nameKo,
	ExerciseDifficulty difficulty,
	String equipment,
	String howTo,
	String targetMuscles,
	String cautions,
	Boolean official,
	Long createdByUserId
) {
	public static ExerciseResponseDto from(Exercise e) {
		return new ExerciseResponseDto(
			e.getId(),
			e.getNameEn(),
			e.getNameKo(),
			e.getDifficulty(),
			e.getEquipment(),
			e.getHowTo(),
			e.getTargetMuscles(),
			e.getCautions(),
			e.isOfficial(),
			e.getCreator() != null ? e.getCreator().getId() : null
		);
	}
}



package com.healthmate.healthmate.domain.exercise.dto.request;

import com.healthmate.healthmate.domain.exercise.enums.ExerciseDifficulty;

public record UpdateExerciseRequestDto(
	String nameEn,
	String nameKo,
	ExerciseDifficulty difficulty,
	String equipment,
	String howTo,
	String targetMuscles,
	String cautions
) {}



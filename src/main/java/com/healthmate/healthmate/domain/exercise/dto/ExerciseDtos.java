package com.healthmate.healthmate.domain.exercise.dto;

import com.healthmate.healthmate.domain.exercise.entity.Difficulty;

public class ExerciseDtos {

	public record CreateExerciseRequest(
		String nameEn,
		String nameKo,
		Difficulty difficulty,
		String equipment,
		String description,
		String targetMuscles,
		String category,
		String caution,
		Long muscleId
	) {
	}

	public record UpdateExerciseRequest(
		String nameEn,
		String nameKo,
		String difficulty,
		String equipment,
		String description,
		String targetMuscles,
		String category,
		String caution,
		Long muscleId
	) {
	}

	public record ExerciseResponse(
		Long id,
		String nameEn,
		String nameKo,
		String difficulty,
		String equipment,
		String howTo,
		String targetMuscles,
		String category,
		String caution,
		Long muscleId
	) {
	}

	public record SearchExerciseRequest(
		String keyword,
		String category,
		String difficulty
	) {
	}
}

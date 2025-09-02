package com.healthmate.healthmate.domain.exercise.dto;

public class ExerciseDtos {

    public record CreateExerciseRequest(
            String name,
            String description,
            String category,
            String difficulty,
            String targetMuscles
    ) {}

    public record UpdateExerciseRequest(
            String name,
            String description,
            String category,
            String difficulty,
            String targetMuscles
    ) {}

    public record ExerciseResponse(
            Long id,
            String name,
            String description,
            String category,
            String difficulty,
            String targetMuscles
    ) {}

    public record SearchExerciseRequest(
            String keyword,
            String category,
            String difficulty
    ) {}
}

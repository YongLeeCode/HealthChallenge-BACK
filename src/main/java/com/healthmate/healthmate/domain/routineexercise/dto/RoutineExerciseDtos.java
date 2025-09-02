package com.healthmate.healthmate.domain.routineexercise.dto;

public class RoutineExerciseDtos {

    public record CreateRoutineExerciseRequest(
            Long exerciseId,
            Integer setNumber,
            Integer countNumber,
            Integer timeExercise
    ) {}

    public record UpdateRoutineExerciseRequest(
            Integer setNumber,
            Integer countNumber,
            Integer timeExercise
    ) {}

    public record RoutineExerciseResponse(
            Long id,
            Long exerciseId,
            String exerciseName,
            String exerciseDescription,
            Integer setNumber,
            Integer countNumber,
            Integer timeExercise,
            Integer orderNumber
    ) {}

    public record ReorderRoutineExerciseRequest(
            Long routineExerciseId,
            Integer newOrderNumber
    ) {}
}

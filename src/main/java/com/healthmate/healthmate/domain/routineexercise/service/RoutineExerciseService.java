package com.healthmate.healthmate.domain.routineexercise.service;

import com.healthmate.healthmate.domain.routine.entity.Routine;
import com.healthmate.healthmate.domain.routineexercise.dto.RoutineExerciseDtos;
import com.healthmate.healthmate.domain.user.entity.User;

import java.util.List;

public interface RoutineExerciseService {
    RoutineExerciseDtos.RoutineExerciseResponse addExerciseToRoutine(User user, Long routineId, RoutineExerciseDtos.CreateRoutineExerciseRequest request);
    List<RoutineExerciseDtos.RoutineExerciseResponse> getRoutineExercises(User user, Long routineId);
    RoutineExerciseDtos.RoutineExerciseResponse updateExercise(User user, Long routineId, Long exerciseId, RoutineExerciseDtos.UpdateRoutineExerciseRequest request);
    void deleteExercise(User user, Long routineId, Long exerciseId);
    void reorderExercise(User user, Long routineId, RoutineExerciseDtos.ReorderRoutineExerciseRequest request);
}

package com.healthmate.healthmate.domain.exercise.service;

import com.healthmate.healthmate.domain.exercise.dto.ExerciseDtos;

import java.util.List;

public interface ExerciseService {
    ExerciseDtos.ExerciseResponse createExercise(ExerciseDtos.CreateExerciseRequest request);
    List<ExerciseDtos.ExerciseResponse> getAllExercises();
    ExerciseDtos.ExerciseResponse getExercise(Long exerciseId);
    ExerciseDtos.ExerciseResponse updateExercise(Long exerciseId, ExerciseDtos.UpdateExerciseRequest request);
    void deleteExercise(Long exerciseId);
    List<ExerciseDtos.ExerciseResponse> searchExercises(ExerciseDtos.SearchExerciseRequest request);
}

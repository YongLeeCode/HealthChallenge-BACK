package com.healthmate.backendv2.exercise.service;

import com.healthmate.backendv2.exercise.dto.ExerciseResponse;
import com.healthmate.backendv2.exercise.dto.ExerciseCreateRequest;
import com.healthmate.backendv2.exercise.dto.ExerciseUpdateRequest;
import com.healthmate.backendv2.exercise.MeasurementType;
import com.healthmate.backendv2.exercise.MuscleFocusArea;
import com.healthmate.backendv2.exercise.ExerciseType;

import java.util.List;

public interface ExerciseService {
    ExerciseResponse getById(Long id);
    List<ExerciseResponse> getAll();
    ExerciseResponse create(ExerciseCreateRequest request);
    ExerciseResponse update(Long id, ExerciseUpdateRequest request);
    void delete(Long id);
    List<ExerciseResponse> getByMuscleFocusArea(MuscleFocusArea muscleFocusArea);
    List<ExerciseResponse> getByExerciseType(ExerciseType exerciseType);
    List<ExerciseResponse> getByMeasurementType(MeasurementType measurementType);
    List<ExerciseResponse> searchByNameOrDescription(String keyword);
    List<ExerciseResponse> getByMuscleFocusAreaAndExerciseType(MuscleFocusArea muscleFocusArea, ExerciseType exerciseType);
}

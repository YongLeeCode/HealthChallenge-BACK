package com.healthmate.healthmate.domain.exercise.service;

import com.healthmate.healthmate.domain.exercise.dto.ExerciseDtos.*;
import com.healthmate.healthmate.domain.exercise.entity.Exercise;
import com.healthmate.healthmate.domain.exercise.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;

    @Override
    @Transactional
    public ExerciseResponse createExercise(CreateExerciseRequest request) {
        Exercise exercise = new Exercise(
                request.name(),
                request.description(),
                request.category(),
                request.difficulty(),
                request.targetMuscles()
        );
        Exercise savedExercise = exerciseRepository.save(exercise);
        return convertToResponse(savedExercise);
    }

    @Override
    public List<ExerciseResponse> getAllExercises() {
        List<Exercise> exercises = exerciseRepository.findAll();
        return exercises.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExerciseResponse getExercise(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("운동을 찾을 수 없습니다."));
        return convertToResponse(exercise);
    }

    @Override
    @Transactional
    public ExerciseResponse updateExercise(Long exerciseId, UpdateExerciseRequest request) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("운동을 찾을 수 없습니다."));
        
        exercise.setName(request.name());
        exercise.setDescription(request.description());
        exercise.setCategory(request.category());
        exercise.setDifficulty(request.difficulty());
        exercise.setTargetMuscles(request.targetMuscles());
        
        return convertToResponse(exercise);
    }

    @Override
    @Transactional
    public void deleteExercise(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("운동을 찾을 수 없습니다."));
        exerciseRepository.delete(exercise);
    }

    @Override
    public List<ExerciseResponse> searchExercises(SearchExerciseRequest request) {
        List<Exercise> exercises;
        
        if (request.keyword() != null && !request.keyword().trim().isEmpty()) {
            exercises = exerciseRepository.searchByKeyword(request.keyword());
        } else if (request.category() != null && !request.category().trim().isEmpty()) {
            exercises = exerciseRepository.findByCategory(request.category());
        } else if (request.difficulty() != null && !request.difficulty().trim().isEmpty()) {
            exercises = exerciseRepository.findByDifficulty(request.difficulty());
        } else {
            exercises = exerciseRepository.findAll();
        }
        
        return exercises.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private ExerciseResponse convertToResponse(Exercise exercise) {
        return new ExerciseResponse(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                exercise.getCategory(),
                exercise.getDifficulty(),
                exercise.getTargetMuscles()
        );
    }
}

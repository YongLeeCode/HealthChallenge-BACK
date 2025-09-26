package com.healthmate.backendv2.exercise.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.healthmate.backendv2.exercise.dto.ExerciseResponse;
import com.healthmate.backendv2.exercise.dto.ExerciseCreateRequest;
import com.healthmate.backendv2.exercise.dto.ExerciseUpdateRequest;
import com.healthmate.backendv2.exercise.entity.Exercise;
import com.healthmate.backendv2.exercise.repository.ExerciseRepository;
import com.healthmate.backendv2.exercise.MeasurementType;
import com.healthmate.backendv2.exercise.MuscleFocusArea;
import com.healthmate.backendv2.exercise.ExerciseType;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;

    @Override
    public ExerciseResponse getById(Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("운동을 찾을 수 없습니다. ID: " + id));
        return ExerciseResponse.from(exercise);
    }

    @Override
    public List<ExerciseResponse> getAll() {
        return exerciseRepository.findAll().stream()
                .map(ExerciseResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public ExerciseResponse create(ExerciseCreateRequest request) {
        // 이름 중복 확인
        if (exerciseRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 운동 이름입니다: " + request.getName());
        }

        Exercise exercise = Exercise.builder()
                .name(request.getName())
                .description(request.getDescription())
                .measurementType(request.getMeasurementType())
                .muscleFocusArea(request.getMuscleFocusArea())
                .exerciseType(request.getExerciseType())
                .imageUrl(request.getImageUrl())
                .build();

        Exercise savedExercise = exerciseRepository.save(exercise);
        return ExerciseResponse.from(savedExercise);
    }

    @Override
    public ExerciseResponse update(Long id, ExerciseUpdateRequest request) {
        Exercise existingExercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("운동을 찾을 수 없습니다. ID: " + id));

        // 이름 중복 확인 (자기 자신 제외)
        if (!existingExercise.getName().equals(request.getName()) && 
            exerciseRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 운동 이름입니다: " + request.getName());
        }

        Exercise updatedExercise = Exercise.builder()
                .id(existingExercise.getId())
                .name(request.getName())
                .description(request.getDescription())
                .measurementType(request.getMeasurementType())
                .muscleFocusArea(request.getMuscleFocusArea())
                .exerciseType(request.getExerciseType())
                .imageUrl(request.getImageUrl())
                .build();

        Exercise savedExercise = exerciseRepository.save(updatedExercise);
        return ExerciseResponse.from(savedExercise);
    }

    @Override
    public void delete(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new IllegalArgumentException("운동을 찾을 수 없습니다. ID: " + id);
        }
        exerciseRepository.deleteById(id);
    }

    @Override
    public List<ExerciseResponse> getByMuscleFocusArea(MuscleFocusArea muscleFocusArea) {
        return exerciseRepository.findByMuscleFocusArea(muscleFocusArea).stream()
                .map(ExerciseResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExerciseResponse> getByExerciseType(ExerciseType exerciseType) {
        return exerciseRepository.findByExerciseType(exerciseType).stream()
                .map(ExerciseResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExerciseResponse> getByMeasurementType(MeasurementType measurementType) {
        return exerciseRepository.findByMeasurementType(measurementType).stream()
                .map(ExerciseResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExerciseResponse> searchByNameOrDescription(String keyword) {
        return exerciseRepository.findByNameOrDescriptionContaining(keyword).stream()
                .map(ExerciseResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExerciseResponse> getByMuscleFocusAreaAndExerciseType(MuscleFocusArea muscleFocusArea, ExerciseType exerciseType) {
        return exerciseRepository.findByMuscleFocusAreaAndExerciseType(muscleFocusArea, exerciseType).stream()
                .map(ExerciseResponse::from)
                .collect(Collectors.toList());
    }
}

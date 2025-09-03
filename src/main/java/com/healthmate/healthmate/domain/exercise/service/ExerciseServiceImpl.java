package com.healthmate.healthmate.domain.exercise.service;

import com.healthmate.healthmate.domain.exercise.dto.ExerciseDtos.*;
import com.healthmate.healthmate.domain.exercise.entity.Difficulty;
import com.healthmate.healthmate.domain.exercise.entity.Exercise;
import com.healthmate.healthmate.domain.exercise.repository.ExerciseRepository;
import com.healthmate.healthmate.domain.muscle.entity.Muscle;
import com.healthmate.healthmate.domain.muscle.repository.MuscleRepository;
import com.healthmate.healthmate.domain.user.entity.User;
import com.healthmate.healthmate.global.security.SecurityUtils;
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
    private final SecurityUtils securityUtils;
    private final MuscleRepository muscleRepository;

    @Override
    @Transactional
    public ExerciseResponse createExercise(CreateExerciseRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        Muscle muscle = null;
        if (request.muscleId() != null) {
            muscle = muscleRepository.findById(request.muscleId())
                    .orElseThrow(() -> new IllegalArgumentException("근육 정보를 찾을 수 없습니다."));
        }
        Exercise exercise = Exercise.builder()
                .nameEn(request.nameEn())
                .nameKo(request.nameKo())
                .difficulty(request.difficulty())
                .equipment(request.equipment())
                .description(request.description())
                .targetMuscles(request.targetMuscles())
                .category(request.category())
                .caution(request.caution())
                .createdBy(currentUser)
                .muscle(muscle)
                .build();
        Exercise savedExercise = exerciseRepository.save(exercise);
        return convertToResponse(savedExercise);
    }

    @Override
    public List<ExerciseResponse> getAllExercises() {
        User currentUser = securityUtils.getCurrentUser();
        List<Exercise> exercises = exerciseRepository.findAll();
        return exercises.stream()
                .filter(e -> e.getCreatedBy().equals(currentUser) || e.getCreatedBy().getRole().name().equals("ADMIN"))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExerciseResponse getExercise(Long exerciseId) {
        User currentUser = securityUtils.getCurrentUser();
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("운동을 찾을 수 없습니다."));
        if (!exercise.getCreatedBy().equals(currentUser) && !exercise.getCreatedBy().getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("조회 권한이 없습니다.");
        }
        return convertToResponse(exercise);
    }

    @Override
    @Transactional
    public ExerciseResponse updateExercise(Long exerciseId, UpdateExerciseRequest request) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("운동을 찾을 수 없습니다."));
        
        exercise.setNameEn(request.nameEn());
        exercise.setNameKo(request.nameKo());
        if (request.difficulty() != null) {
            exercise.setDifficulty(Difficulty.valueOf(request.difficulty()));
        }
        if (request.muscleId() != null) {
            Muscle muscle = muscleRepository.findById(request.muscleId())
                    .orElseThrow(() -> new IllegalArgumentException("근육 정보를 찾을 수 없습니다."));
            exercise.setMuscle(muscle);
        }
        exercise.setEquipment(request.equipment());
        exercise.setDescription(request.description());
        exercise.setTargetMuscles(request.targetMuscles());
        exercise.setCategory(request.category());
        exercise.setCaution(request.caution());
        
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
        User currentUser = securityUtils.getCurrentUser();
        
        if (request.keyword() != null && !request.keyword().trim().isEmpty()) {
            exercises = exerciseRepository.searchByKeyword(request.keyword(), currentUser);
        } else if (request.category() != null && !request.category().trim().isEmpty()) {
            exercises = exerciseRepository.findByCategory(request.category());
        } else if (request.difficulty() != null && !request.difficulty().trim().isEmpty()) {
            exercises = exerciseRepository.findByDifficulty(Difficulty.valueOf(request.difficulty()));
        } else {
            exercises = exerciseRepository.findAll().stream()
                    .filter(e -> e.getCreatedBy().equals(currentUser) || e.getCreatedBy().getRole().name().equals("ADMIN"))
                    .collect(Collectors.toList());
        }
        
        return exercises.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private ExerciseResponse convertToResponse(Exercise exercise) {
        return new ExerciseResponse(
                exercise.getId(),
                exercise.getNameEn(),
                exercise.getNameKo(),
                exercise.getDifficulty().name(),
                exercise.getEquipment(),
                exercise.getDescription(),
                exercise.getTargetMuscles(),
                exercise.getCategory(),
                exercise.getCaution(),
                exercise.getMuscle() != null ? exercise.getMuscle().getId() : null
        );
    }
}

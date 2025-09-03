package com.healthmate.healthmate.domain.routineexercise.service;

import com.healthmate.healthmate.domain.exercise.entity.Exercise;
import com.healthmate.healthmate.domain.exercise.repository.ExerciseRepository;
import com.healthmate.healthmate.domain.routine.entity.Routine;
import com.healthmate.healthmate.domain.routine.repository.RoutineRepository;
import com.healthmate.healthmate.domain.routineexercise.dto.RoutineExerciseDtos.*;
import com.healthmate.healthmate.domain.routineexercise.entity.RoutineExercise;
import com.healthmate.healthmate.domain.routineexercise.repository.RoutineExerciseRepository;
import com.healthmate.healthmate.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineExerciseServiceImpl implements RoutineExerciseService {

    private final RoutineExerciseRepository routineExerciseRepository;
    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;

    @Override
    @Transactional
    public RoutineExerciseResponse addExerciseToRoutine(User user, Long routineId, CreateRoutineExerciseRequest request) {
        Routine routine = routineRepository.findByIdAndUser(routineId, user)
                .orElseThrow(() -> new IllegalArgumentException("루틴을 찾을 수 없습니다."));
        
        Exercise exercise = exerciseRepository.findById(request.exerciseId())
                .orElseThrow(() -> new IllegalArgumentException("운동을 찾을 수 없습니다."));
        
        Integer nextOrderNumber = routineExerciseRepository.findMaxOrderNumberByRoutine(routine);
        if (nextOrderNumber == null) {
            nextOrderNumber = 0;
        } else {
            nextOrderNumber++;
        }
        
        RoutineExercise routineExercise = new RoutineExercise(
                routine, 
                exercise, 
                request.setNumber(), 
                request.countNumber(), 
                request.timeExercise(), 
                nextOrderNumber
        );
        
        RoutineExercise savedRoutineExercise = routineExerciseRepository.save(routineExercise);
        return convertToResponse(savedRoutineExercise);
    }

    @Override
    public List<RoutineExerciseResponse> getRoutineExercises(User user, Long routineId) {
        Routine routine = routineRepository.findByIdAndUser(routineId, user)
                .orElseThrow(() -> new IllegalArgumentException("루틴을 찾을 수 없습니다."));
        
        List<RoutineExercise> routineExercises = routineExerciseRepository.findByRoutineOrderByOrderNumberAsc(routine);
        return routineExercises.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoutineExerciseResponse updateExercise(User user, Long routineId, Long exerciseId, UpdateRoutineExerciseRequest request) {
        Routine routine = routineRepository.findByIdAndUser(routineId, user)
                .orElseThrow(() -> new IllegalArgumentException("루틴을 찾을 수 없습니다."));
        
        RoutineExercise routineExercise = routineExerciseRepository.findByIdAndRoutine(exerciseId, routine)
                .orElseThrow(() -> new IllegalArgumentException("루틴 운동을 찾을 수 없습니다."));
        
        routineExercise.setSetNumber(request.setNumber());
        routineExercise.setCountNumber(request.countNumber());
        routineExercise.setTimeExercise(request.timeExercise());
        
        return convertToResponse(routineExercise);
    }

    @Override
    @Transactional
    public void deleteExercise(User user, Long routineId, Long exerciseId) {
        Routine routine = routineRepository.findByIdAndUser(routineId, user)
                .orElseThrow(() -> new IllegalArgumentException("루틴을 찾을 수 없습니다."));
        
        RoutineExercise routineExercise = routineExerciseRepository.findByIdAndRoutine(exerciseId, routine)
                .orElseThrow(() -> new IllegalArgumentException("루틴 운동을 찾을 수 없습니다."));
        
        routineExerciseRepository.delete(routineExercise);
    }

    @Override
    @Transactional
    public void reorderExercise(User user, Long routineId, ReorderRoutineExerciseRequest request) {
        Routine routine = routineRepository.findByIdAndUser(routineId, user)
                .orElseThrow(() -> new IllegalArgumentException("루틴을 찾을 수 없습니다."));
        
        RoutineExercise routineExercise = routineExerciseRepository.findByIdAndRoutine(request.routineExerciseId(), routine)
                .orElseThrow(() -> new IllegalArgumentException("루틴 운동을 찾을 수 없습니다."));
        
        routineExercise.setOrderNumber(request.newOrderNumber());
    }

    private RoutineExerciseResponse convertToResponse(RoutineExercise routineExercise) {
        return new RoutineExerciseResponse(
                routineExercise.getId(),
                routineExercise.getExercise().getId(),
                routineExercise.getExercise().getNameKo(),
                routineExercise.getExercise().getDescription(),
                routineExercise.getSetNumber(),
                routineExercise.getCountNumber(),
                routineExercise.getTimeExercise(),
                routineExercise.getOrderNumber()
        );
    }
}

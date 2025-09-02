package com.healthmate.healthmate.domain.routine.service;

import com.healthmate.healthmate.domain.routine.dto.RoutineDtos.*;

import com.healthmate.healthmate.domain.routine.entity.Routine;
import com.healthmate.healthmate.domain.routine.repository.RoutineRepository;
import com.healthmate.healthmate.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineServiceImpl implements RoutineService {

    private final RoutineRepository routineRepository;

    @Override
    @Transactional
    public RoutineResponse createRoutine(User user, CreateRoutineRequest request) {
        Routine routine = new Routine(user, request.name());
        Routine savedRoutine = routineRepository.save(routine);
        return convertToResponse(savedRoutine);
    }

    @Override
    public List<RoutineResponse> getUserRoutines(User user) {
        List<Routine> routines = routineRepository.findByUser(user);
        return routines.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoutineResponse updateRoutine(User user, Long routineId, UpdateRoutineRequest request) {
        Routine routine = routineRepository.findByIdAndUser(routineId, user)
                .orElseThrow(() -> new IllegalArgumentException("루틴을 찾을 수 없습니다."));
        routine.setName(request.name());
        return convertToResponse(routine);
    }

    @Override
    @Transactional
    public void deleteRoutine(User user, Long routineId) {
        Routine routine = routineRepository.findByIdAndUser(routineId, user)
                .orElseThrow(() -> new IllegalArgumentException("루틴을 찾을 수 없습니다."));
        routineRepository.delete(routine);
    }

    private RoutineResponse convertToResponse(Routine routine) {
        return new RoutineResponse(
                routine.getId(),
                routine.getName(),
                routine.getUser().getId()
        );
    }
}

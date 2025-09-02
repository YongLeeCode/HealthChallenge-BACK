package com.healthmate.healthmate.domain.routine.service;

import com.healthmate.healthmate.domain.routine.dto.RoutineDtos;
import com.healthmate.healthmate.domain.user.entity.User;

import java.util.List;

public interface RoutineService {
    RoutineDtos.RoutineResponse createRoutine(User user, RoutineDtos.CreateRoutineRequest request);
    List<RoutineDtos.RoutineResponse> getUserRoutines(User user);
    RoutineDtos.RoutineResponse updateRoutine(User user, Long routineId, RoutineDtos.UpdateRoutineRequest request);
    void deleteRoutine(User user, Long routineId);
}

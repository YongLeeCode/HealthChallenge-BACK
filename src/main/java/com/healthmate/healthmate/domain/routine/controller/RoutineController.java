package com.healthmate.healthmate.domain.routine.controller;

import com.healthmate.healthmate.domain.routine.dto.RoutineDtos;
import com.healthmate.healthmate.domain.routine.service.RoutineService;
import com.healthmate.healthmate.domain.user.entity.User;
import com.healthmate.healthmate.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routines")
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineService routineService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<RoutineDtos.RoutineResponse> createRoutine(@RequestBody RoutineDtos.CreateRoutineRequest request) {
        User user = securityUtils.getCurrentUser();
        RoutineDtos.RoutineResponse response = routineService.createRoutine(user, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RoutineDtos.RoutineResponse>> getUserRoutines() {
        User user = securityUtils.getCurrentUser();
        List<RoutineDtos.RoutineResponse> routines = routineService.getUserRoutines(user);
        return ResponseEntity.ok(routines);
    }

    @PatchMapping("/{routineId}")
    public ResponseEntity<RoutineDtos.RoutineResponse> updateRoutine(
            @PathVariable Long routineId,
            @RequestBody RoutineDtos.UpdateRoutineRequest request) {
        User user = securityUtils.getCurrentUser();
        RoutineDtos.RoutineResponse response = routineService.updateRoutine(user, routineId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{routineId}")
    public ResponseEntity<Void> deleteRoutine(@PathVariable Long routineId) {
        User user = securityUtils.getCurrentUser();
        routineService.deleteRoutine(user, routineId);
        return ResponseEntity.noContent().build();
    }
}

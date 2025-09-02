package com.healthmate.healthmate.domain.routineexercise.controller;

import com.healthmate.healthmate.domain.routineexercise.dto.RoutineExerciseDtos;
import com.healthmate.healthmate.domain.routineexercise.service.RoutineExerciseService;
import com.healthmate.healthmate.domain.user.entity.User;
import com.healthmate.healthmate.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routines/{routineId}/exercises")
@RequiredArgsConstructor
public class RoutineExerciseController {

    private final RoutineExerciseService routineExerciseService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<RoutineExerciseDtos.RoutineExerciseResponse> addExerciseToRoutine(
            @PathVariable Long routineId,
            @RequestBody RoutineExerciseDtos.CreateRoutineExerciseRequest request) {
        User user = securityUtils.getCurrentUser();
        RoutineExerciseDtos.RoutineExerciseResponse response = routineExerciseService.addExerciseToRoutine(user, routineId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RoutineExerciseDtos.RoutineExerciseResponse>> getRoutineExercises(@PathVariable Long routineId) {
        User user = securityUtils.getCurrentUser();
        List<RoutineExerciseDtos.RoutineExerciseResponse> exercises = routineExerciseService.getRoutineExercises(user, routineId);
        return ResponseEntity.ok(exercises);
    }

    @PutMapping("/{exerciseId}")
    public ResponseEntity<RoutineExerciseDtos.RoutineExerciseResponse> updateExercise(
            @PathVariable Long routineId,
            @PathVariable Long exerciseId,
            @RequestBody RoutineExerciseDtos.UpdateRoutineExerciseRequest request) {
        User user = securityUtils.getCurrentUser();
        RoutineExerciseDtos.RoutineExerciseResponse response = routineExerciseService.updateExercise(user, routineId, exerciseId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{exerciseId}")
    public ResponseEntity<Void> deleteExercise(
            @PathVariable Long routineId,
            @PathVariable Long exerciseId) {
        User user = securityUtils.getCurrentUser();
        routineExerciseService.deleteExercise(user, routineId, exerciseId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reorder")
    public ResponseEntity<Void> reorderExercise(
            @PathVariable Long routineId,
            @RequestBody RoutineExerciseDtos.ReorderRoutineExerciseRequest request) {
        User user = securityUtils.getCurrentUser();
        routineExerciseService.reorderExercise(user, routineId, request);
        return ResponseEntity.ok().build();
    }
}

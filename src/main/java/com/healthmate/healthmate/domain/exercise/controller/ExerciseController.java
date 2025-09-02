package com.healthmate.healthmate.domain.exercise.controller;

import com.healthmate.healthmate.domain.exercise.dto.ExerciseDtos;
import com.healthmate.healthmate.domain.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<ExerciseDtos.ExerciseResponse> createExercise(@RequestBody ExerciseDtos.CreateExerciseRequest request) {
        ExerciseDtos.ExerciseResponse response = exerciseService.createExercise(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ExerciseDtos.ExerciseResponse>> getAllExercises() {
        List<ExerciseDtos.ExerciseResponse> exercises = exerciseService.getAllExercises();
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{exerciseId}")
    public ResponseEntity<ExerciseDtos.ExerciseResponse> getExercise(@PathVariable Long exerciseId) {
        ExerciseDtos.ExerciseResponse exercise = exerciseService.getExercise(exerciseId);
        return ResponseEntity.ok(exercise);
    }

    @PutMapping("/{exerciseId}")
    public ResponseEntity<ExerciseDtos.ExerciseResponse> updateExercise(
            @PathVariable Long exerciseId,
            @RequestBody ExerciseDtos.UpdateExerciseRequest request) {
        ExerciseDtos.ExerciseResponse response = exerciseService.updateExercise(exerciseId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{exerciseId}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long exerciseId) {
        exerciseService.deleteExercise(exerciseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ExerciseDtos.ExerciseResponse>> searchExercises(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty) {
        ExerciseDtos.SearchExerciseRequest request = new ExerciseDtos.SearchExerciseRequest(keyword, category, difficulty);
        List<ExerciseDtos.ExerciseResponse> exercises = exerciseService.searchExercises(request);
        return ResponseEntity.ok(exercises);
    }
}

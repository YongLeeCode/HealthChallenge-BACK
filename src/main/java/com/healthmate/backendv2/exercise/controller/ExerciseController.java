package com.healthmate.backendv2.exercise.controller;

import lombok.RequiredArgsConstructor;

import com.healthmate.backendv2.exercise.dto.ExerciseResponse;
import com.healthmate.backendv2.exercise.dto.ExerciseCreateRequest;
import com.healthmate.backendv2.exercise.dto.ExerciseUpdateRequest;
import com.healthmate.backendv2.exercise.service.ExerciseService;
import com.healthmate.backendv2.exercise.MeasurementType;
import com.healthmate.backendv2.exercise.MuscleFocusArea;
import com.healthmate.backendv2.exercise.ExerciseType;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> getAll() {
        return ResponseEntity.ok(exerciseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(exerciseService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ExerciseResponse> create(@RequestBody ExerciseCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(exerciseService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponse> update(@PathVariable Long id, 
                                                  @RequestBody ExerciseUpdateRequest request) {
        return ResponseEntity.ok(exerciseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        exerciseService.delete(id);
        return ResponseEntity.ok("운동이 삭제되었습니다.");
    }

    @GetMapping("/search")
    public ResponseEntity<List<ExerciseResponse>> searchByNameOrDescription(@RequestParam String keyword) {
        return ResponseEntity.ok(exerciseService.searchByNameOrDescription(keyword));
    }

    @GetMapping("/muscle-focus/{muscleFocusArea}")
    public ResponseEntity<List<ExerciseResponse>> getByMuscleFocusArea(@PathVariable MuscleFocusArea muscleFocusArea) {
        return ResponseEntity.ok(exerciseService.getByMuscleFocusArea(muscleFocusArea));
    }

    @GetMapping("/exercise-type/{exerciseType}")
    public ResponseEntity<List<ExerciseResponse>> getByExerciseType(@PathVariable ExerciseType exerciseType) {
        return ResponseEntity.ok(exerciseService.getByExerciseType(exerciseType));
    }

    @GetMapping("/measurement-type/{measurementType}")
    public ResponseEntity<List<ExerciseResponse>> getByMeasurementType(@PathVariable MeasurementType measurementType) {
        return ResponseEntity.ok(exerciseService.getByMeasurementType(measurementType));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ExerciseResponse>> getByMuscleFocusAreaAndExerciseType(
            @RequestParam MuscleFocusArea muscleFocusArea,
            @RequestParam ExerciseType exerciseType) {
        return ResponseEntity.ok(exerciseService.getByMuscleFocusAreaAndExerciseType(muscleFocusArea, exerciseType));
    }
}

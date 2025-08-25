package com.healthmate.healthmate.domain.exercise.controller;

import com.healthmate.healthmate.domain.exercise.dto.request.AddExerciseRequestDto;
import com.healthmate.healthmate.domain.exercise.dto.request.UpdateExerciseRequestDto;
import com.healthmate.healthmate.domain.exercise.dto.response.ExerciseResponseDto;
import com.healthmate.healthmate.domain.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
public class ExerciseController {
	private final ExerciseService exerciseService;

	@PostMapping
	public ResponseEntity<Long> add(@RequestBody AddExerciseRequestDto req) {
		return ResponseEntity.ok(exerciseService.add(req));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody UpdateExerciseRequestDto req) {
		exerciseService.update(id, req);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		exerciseService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<ExerciseResponseDto> get(@PathVariable Long id) {
		return ResponseEntity.ok(exerciseService.get(id));
	}

	@GetMapping
	public ResponseEntity<List<ExerciseResponseDto>> list() {
		return ResponseEntity.ok(exerciseService.list());
	}
}



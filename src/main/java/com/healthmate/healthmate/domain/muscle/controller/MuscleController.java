package com.healthmate.healthmate.domain.muscle.controller;

import com.healthmate.healthmate.domain.muscle.dto.MuscleDtos.*;
import com.healthmate.healthmate.domain.muscle.service.MuscleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/muscles")
@RequiredArgsConstructor
public class MuscleController {

	private final MuscleService muscleService;

	@PostMapping
	public ResponseEntity<MuscleResponse> create(@RequestBody CreateMuscleRequest request) {
		return ResponseEntity.ok(muscleService.createMuscle(request));
	}

	@PutMapping("/{muscleId}")
	public ResponseEntity<MuscleResponse> update(
			@PathVariable Long muscleId,
			@RequestBody UpdateMuscleRequest request) {
		return ResponseEntity.ok(muscleService.updateMuscle(muscleId, request));
	}

	@DeleteMapping("/{muscleId}")
	public ResponseEntity<Void> delete(@PathVariable Long muscleId) {
		muscleService.deleteMuscle(muscleId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<List<MuscleResponse>> listAll() {
		return ResponseEntity.ok(muscleService.getAll());
	}
}



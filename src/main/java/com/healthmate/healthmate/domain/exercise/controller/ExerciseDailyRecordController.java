package com.healthmate.healthmate.domain.exercise.controller;

import com.healthmate.healthmate.domain.exercise.dto.request.AddExerciseDailyRecordRequestDto;
import com.healthmate.healthmate.domain.exercise.dto.request.UpdateExerciseDailyRecordRequestDto;
import com.healthmate.healthmate.domain.exercise.dto.response.ExerciseDailyRecordResponseDto;
import com.healthmate.healthmate.domain.exercise.service.ExerciseDailyRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/exercise-daily-records")
@RequiredArgsConstructor
public class ExerciseDailyRecordController {
	private final ExerciseDailyRecordService dailyRecordService;

	@PostMapping
	public ResponseEntity<Long> upsert(@RequestBody AddExerciseDailyRecordRequestDto req) {
		return ResponseEntity.ok(dailyRecordService.upsert(req));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody UpdateExerciseDailyRecordRequestDto req) {
		dailyRecordService.update(id, req);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		dailyRecordService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<ExerciseDailyRecordResponseDto> get(@PathVariable Long id) {
		return ResponseEntity.ok(dailyRecordService.get(id));
	}

	@GetMapping("/users/{userId}/date")
	public ResponseEntity<ExerciseDailyRecordResponseDto> getByUserAndDate(
		@PathVariable Long userId,
		@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
	) {
		return ResponseEntity.ok(dailyRecordService.getByUserAndDate(userId, date));
	}

	@GetMapping("/users/{userId}")
	public ResponseEntity<List<ExerciseDailyRecordResponseDto>> listByUser(@PathVariable Long userId) {
		return ResponseEntity.ok(dailyRecordService.listByUser(userId));
	}
}


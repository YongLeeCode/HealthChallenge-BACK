package com.healthmate.healthmate.domain.preference.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.healthmate.healthmate.domain.preference.dto.request.AddPreferenceRequestDto;
import com.healthmate.healthmate.domain.preference.dto.request.UpdatePreferenceRequestDto;
import com.healthmate.healthmate.domain.preference.dto.response.PreferenceResponseDto;
import com.healthmate.healthmate.domain.preference.service.PreferenceService;
import java.util.List;

@RestController
@RequestMapping("/preferences")
@RequiredArgsConstructor
public class PreferenceController {
	private final PreferenceService preferenceService;

	@PostMapping
	public ResponseEntity<Long> addPreference(@RequestBody AddPreferenceRequestDto req) {
		return ResponseEntity.ok(preferenceService.addPreference(req));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updatePreference(@PathVariable Long id, @RequestBody UpdatePreferenceRequestDto req) {
		preferenceService.updatePreference(id, req);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePreference(@PathVariable Long id) {
		preferenceService.deletePreference(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<PreferenceResponseDto> getPreference(@PathVariable Long id) {
		return ResponseEntity.ok(preferenceService.getPreference(id));
	}

	@GetMapping
	public ResponseEntity<List<PreferenceResponseDto>> getPreferencesByExercise(@RequestParam Integer exerciseId) {
		return ResponseEntity.ok(preferenceService.getPreferencesByExercise(exerciseId));
	}

}

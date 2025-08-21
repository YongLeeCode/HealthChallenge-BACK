package com.healthmate.healthmate.domain.preference.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthmate.healthmate.domain.preference.dto.request.AddPreferenceRequestDto;
import com.healthmate.healthmate.domain.preference.service.PreferenceService;

@RestController
@RequestMapping("/preferences")
@RequiredArgsConstructor
public class PreferenceController {
	private final PreferenceService preferenceService;

	@PostMapping
	public ResponseEntity<Long> addPreference(@RequestBody AddPreferenceRequestDto req) {
		return ResponseEntity.ok(preferenceService.addPreference(req));
	}

}

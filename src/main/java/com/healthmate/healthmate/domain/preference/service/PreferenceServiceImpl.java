package com.healthmate.healthmate.domain.preference.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.healthmate.healthmate.domain.preference.dto.request.AddPreferenceRequestDto;
import com.healthmate.healthmate.domain.preference.entity.Preference;
import com.healthmate.healthmate.domain.preference.repository.PreferenceRepository;

@Service
@RequiredArgsConstructor
public class PreferenceServiceImpl implements PreferenceService{
	private final PreferenceRepository preferenceRepository;

	@Override
	public Long addPreference(AddPreferenceRequestDto req) {
		Preference preference = new Preference(req.exerciseId(), req.preference());
		return preferenceRepository.save(preference).getId();
	}
}

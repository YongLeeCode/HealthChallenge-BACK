package com.healthmate.healthmate.domain.preference.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.healthmate.healthmate.domain.preference.dto.request.AddPreferenceRequestDto;
import com.healthmate.healthmate.domain.preference.dto.request.UpdatePreferenceRequestDto;
import com.healthmate.healthmate.domain.preference.entity.Preference;
import com.healthmate.healthmate.domain.preference.repository.PreferenceRepository;
import com.healthmate.healthmate.domain.preference.dto.response.PreferenceResponseDto;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreferenceServiceImpl implements PreferenceService{
	private final PreferenceRepository preferenceRepository;

	@Override
	public Long addPreference(AddPreferenceRequestDto req) {
		Preference preference = new Preference(req.exerciseId(), req.preference());
		return preferenceRepository.save(preference).getId();
	}

	@Override
	public void updatePreference(Long id, UpdatePreferenceRequestDto req) {
		Preference preference = preferenceRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Preference not found: " + id));
		if (req.exerciseId() != null) {
			preference.setExerciseId(req.exerciseId());
		}
		if (req.preference() != null) {
			preference.setPreference(req.preference());
		}
		preferenceRepository.save(preference);
	}

	@Override
	public void deletePreference(Long id) {
		if (!preferenceRepository.existsById(id)) {
			throw new IllegalArgumentException("Preference not found: " + id);
		}
		preferenceRepository.deleteById(id);
	}

	@Override
	public PreferenceResponseDto getPreference(Long id) {
		Preference preference = preferenceRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Preference not found: " + id));
		return PreferenceResponseDto.from(preference);
	}

	@Override
	public List<PreferenceResponseDto> getPreferencesByExercise(Integer exerciseId) {
		return preferenceRepository.findAllByExerciseId(exerciseId)
			.stream()
			.map(PreferenceResponseDto::from)
			.collect(Collectors.toList());
	}
}

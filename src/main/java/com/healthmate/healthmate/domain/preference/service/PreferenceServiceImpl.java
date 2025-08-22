package com.healthmate.healthmate.domain.preference.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.healthmate.healthmate.domain.preference.dto.request.AddPreferenceRequestDto;
import com.healthmate.healthmate.domain.preference.dto.request.UpdatePreferenceRequestDto;
import com.healthmate.healthmate.domain.preference.entity.Preference;
import com.healthmate.healthmate.domain.preference.repository.PreferenceRepository;
import com.healthmate.healthmate.domain.preference.dto.response.PreferenceResponseDto;
import com.healthmate.healthmate.domain.exercise.entity.Exercise;
import com.healthmate.healthmate.domain.exercise.repository.ExerciseRepository;
import com.healthmate.healthmate.domain.user.entity.User;
import com.healthmate.healthmate.domain.user.repository.UserRepository;
import com.healthmate.healthmate.global.security.SecurityUtils;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreferenceServiceImpl implements PreferenceService{
	private final PreferenceRepository preferenceRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

	@Override
	public Long addPreference(AddPreferenceRequestDto req) {
		Long userId = SecurityUtils.getCurrentUserIdOrThrow();
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalStateException("User not found: " + userId));
		Exercise exercise = exerciseRepository.findById(req.exerciseId())
			.orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + req.exerciseId()));
		Preference preference = new Preference(exercise, req.preference());
		preference.setUser(user);
		return preferenceRepository.save(preference).getId();
	}

	@Override
	public void updatePreference(Long id, UpdatePreferenceRequestDto req) {
		Preference preference = preferenceRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Preference not found: " + id));
		Long userId = SecurityUtils.getCurrentUserIdOrThrow();
		if (!preference.getUser().getId().equals(userId)) {
			throw new IllegalStateException("Forbidden");
		}
		if (req.exerciseId() != null) {
			Exercise exercise = exerciseRepository.findById(req.exerciseId())
				.orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + req.exerciseId()));
			preference.setExercise(exercise);
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
		Preference p = preferenceRepository.findById(id).get();
		Long userId = SecurityUtils.getCurrentUserIdOrThrow();
		if (!p.getUser().getId().equals(userId)) {
			throw new IllegalStateException("Forbidden");
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
	public List<PreferenceResponseDto> getPreferencesByExercise(Long exerciseId) {
		Long userId = SecurityUtils.getCurrentUserIdOrThrow();
		return preferenceRepository.findAllByUser_IdAndExercise_Id(userId, exerciseId)
			.stream()
			.map(PreferenceResponseDto::from)
			.collect(Collectors.toList());
	}
}

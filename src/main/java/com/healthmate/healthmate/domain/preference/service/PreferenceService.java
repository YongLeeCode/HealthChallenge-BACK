package com.healthmate.healthmate.domain.preference.service;

import com.healthmate.healthmate.domain.preference.dto.request.AddPreferenceRequestDto;
import com.healthmate.healthmate.domain.preference.dto.request.UpdatePreferenceRequestDto;
import com.healthmate.healthmate.domain.preference.dto.response.PreferenceResponseDto;
import java.util.List;

public interface PreferenceService {
	Long addPreference(AddPreferenceRequestDto req);
	void updatePreference(Long id, UpdatePreferenceRequestDto req);
	void deletePreference(Long id);
	PreferenceResponseDto getPreference(Long id);
	List<PreferenceResponseDto> getPreferencesByExercise(Long exerciseId);
}

package com.healthmate.healthmate.domain.preference.dto.response;

import com.healthmate.healthmate.domain.preference.entity.Preference;
import com.healthmate.healthmate.domain.preference.enums.PreferenceEnum;

public record PreferenceResponseDto(
	Long id,
	Integer exerciseId,
	PreferenceEnum preference
) {
	public static PreferenceResponseDto from(Preference entity) {
		return new PreferenceResponseDto(entity.getId(), entity.getExerciseId(), entity.getPreference());
	}
}



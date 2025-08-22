package com.healthmate.healthmate.domain.preference.dto.request;

import com.healthmate.healthmate.domain.preference.enums.PreferenceEnum;

public record UpdatePreferenceRequestDto(
	Long exerciseId,
	PreferenceEnum preference
) {
}



package com.healthmate.healthmate.domain.preference.dto.request;

import com.healthmate.healthmate.domain.preference.enums.PreferenceEnum;

public record AddPreferenceRequestDto(
	int exerciseId,
	PreferenceEnum preference
) {
}

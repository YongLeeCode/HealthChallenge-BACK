package com.healthmate.healthmate.domain.preference.service;

import com.healthmate.healthmate.domain.preference.dto.request.AddPreferenceRequestDto;

public interface PreferenceService {
	Long addPreference(AddPreferenceRequestDto req);
}

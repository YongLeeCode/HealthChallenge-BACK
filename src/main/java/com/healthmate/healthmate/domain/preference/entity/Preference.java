package com.healthmate.healthmate.domain.preference.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.healthmate.healthmate.domain.preference.enums.PreferenceEnum;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Preference {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 추후 유저 추가 예정
	// Integer userId;

	//mapping
	Integer exerciseId;

	@Enumerated(EnumType.STRING)
	PreferenceEnum preference;

	public Preference(Integer exerciseId, PreferenceEnum preference) {
		this.exerciseId = exerciseId;
		this.preference = preference;
	}
}

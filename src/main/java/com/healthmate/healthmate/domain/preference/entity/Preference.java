package com.healthmate.healthmate.domain.preference.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.healthmate.healthmate.domain.preference.enums.PreferenceEnum;
import com.healthmate.healthmate.domain.user.entity.User;
import com.healthmate.healthmate.domain.exercise.entity.Exercise;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Preference {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Exercise exercise;

	@Enumerated(EnumType.STRING)
	PreferenceEnum preference;

	public Preference(Exercise exercise, PreferenceEnum preference) {
		this.exercise = exercise;
		this.preference = preference;
	}
}

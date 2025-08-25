package com.healthmate.healthmate.domain.exercise.entity;

import com.healthmate.healthmate.domain.exercise.enums.ExerciseDifficulty;
import com.healthmate.healthmate.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Exercise {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200)
	private String nameEn;

	@Column(nullable = false, length = 200)
	private String nameKo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ExerciseDifficulty difficulty;

	@Column(length = 200)
	private String equipment;

	@Column(length = 1000)
	private String howTo;

	@Column(length = 500)
	private String targetMuscles;

	@Column(length = 1000)
	private String cautions;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator_user_id")
	private User creator;

	@Column(nullable = false)
	private boolean official = false;

	public Exercise(String nameEn, String nameKo, ExerciseDifficulty difficulty,
			String equipment, String howTo, String targetMuscles, String cautions) {
		this.nameEn = nameEn;
		this.nameKo = nameKo;
		this.difficulty = difficulty;
		this.equipment = equipment;
		this.howTo = howTo;
		this.targetMuscles = targetMuscles;
		this.cautions = cautions;
	}
}



package com.healthmate.healthmate.domain.exercise.entity;

import com.healthmate.healthmate.domain.muscle.entity.Muscle;
import com.healthmate.healthmate.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exercise {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name_en", nullable = false)
	private String nameEn;

	@Column(name = "name_ko", nullable = false)
	private String nameKo;

	@Enumerated(EnumType.STRING)
	@Column(name = "difficulty", nullable = false)
	private Difficulty difficulty;

	@Column(name = "equipment")
	private String equipment;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	// Kept for backward compatibility; will be superseded by muscles relation
	@Column(name = "target_muscles")
	private String targetMuscles;

	@Column(name = "category")
	private String category;

	@Column(name = "caution")
	private String caution;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_user_id", nullable = false)
	private User createdBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "muscle_id")
	private Muscle muscle;

	@Builder
	public Exercise(String nameEn,
			String nameKo,
			Difficulty difficulty,
			String equipment,
			String description,
			String targetMuscles,
			String category,
			String caution,
			User createdBy,
			Muscle muscle) {
		this.nameEn = nameEn;
		this.nameKo = nameKo;
		this.difficulty = difficulty;
		this.equipment = equipment;
		this.description = description;
		this.targetMuscles = targetMuscles;
		this.category = category;
		this.caution = caution;
		this.createdBy = createdBy;
		this.muscle = muscle;
	}
}



package com.healthmate.healthmate.domain.muscle.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "muscles")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Muscle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "kor_name", nullable = false)
	private String korName;

	@Column(name = "eng_name", nullable = false)
	private String engName;

	@Column(name = "category", nullable = false)
	private String category; // 예: 가슴, 어깨, 등, 허벅지 등

	@Builder
	public Muscle(String korName, String engName, String category) {
		this.korName = korName;
		this.engName = engName;
		this.category = category;
	}
}



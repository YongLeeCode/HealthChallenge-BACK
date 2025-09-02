package com.healthmate.healthmate.domain.exercise.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 50)
    private String category; // 예: 상체, 하체, 코어, 유산소 등

    @Column(length = 50)
    private String difficulty; // 예: 초급, 중급, 고급

    @Column(length = 200)
    private String targetMuscles; // 타겟 근육군

    public Exercise(String name, String description, String category, String difficulty, String targetMuscles) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.targetMuscles = targetMuscles;
    }
}

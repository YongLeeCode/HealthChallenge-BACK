package com.healthmate.backendv2.exercise.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.google.common.annotations.VisibleForTesting;
import com.healthmate.backendv2.exercise.MeasurementType;
import com.healthmate.backendv2.exercise.MuscleFocusArea;
import com.healthmate.backendv2.exercise.ExerciseType;

@Entity
@Table(name = "exercises")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_type", nullable = false, length = 20)
    private MeasurementType measurementType;

    @Enumerated(EnumType.STRING)
    @Column(name = "muscle_focus_area", nullable = false, length = 20)
    private MuscleFocusArea muscleFocusArea;

    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_type", nullable = false, length = 20)
    private ExerciseType exerciseType;

    @Column(name = "image_url", length = 500)
    private String imageUrl;


    // 테스트 전용 생성자 (테스트 패키지에서만 접근 가능하게)
    @VisibleForTesting
    public Exercise(Long id, String name, String description, MeasurementType measurementType, 
                   MuscleFocusArea muscleFocusArea, ExerciseType exerciseType, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.measurementType = measurementType;
        this.muscleFocusArea = muscleFocusArea;
        this.exerciseType = exerciseType;
        this.imageUrl = imageUrl;
    }
}

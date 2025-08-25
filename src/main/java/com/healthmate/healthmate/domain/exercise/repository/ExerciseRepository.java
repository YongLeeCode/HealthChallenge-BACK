package com.healthmate.healthmate.domain.exercise.repository;

import com.healthmate.healthmate.domain.exercise.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}



package com.healthmate.backendv2.exercise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import com.healthmate.backendv2.exercise.entity.Exercise;
import com.healthmate.backendv2.exercise.MeasurementType;
import com.healthmate.backendv2.exercise.MuscleFocusArea;
import com.healthmate.backendv2.exercise.ExerciseType;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    
    Optional<Exercise> findByName(String name);
    
    boolean existsByName(String name);
    
    List<Exercise> findByMuscleFocusArea(MuscleFocusArea muscleFocusArea);
    
    List<Exercise> findByExerciseType(ExerciseType exerciseType);
    
    List<Exercise> findByMeasurementType(MeasurementType measurementType);
    
    @Query("SELECT e FROM Exercise e WHERE e.muscleFocusArea = :muscleFocusArea AND e.exerciseType = :exerciseType")
    List<Exercise> findByMuscleFocusAreaAndExerciseType(@Param("muscleFocusArea") MuscleFocusArea muscleFocusArea, 
                                                       @Param("exerciseType") ExerciseType exerciseType);
    
    @Query("SELECT e FROM Exercise e WHERE e.name LIKE %:keyword% OR e.description LIKE %:keyword%")
    List<Exercise> findByNameOrDescriptionContaining(@Param("keyword") String keyword);
}

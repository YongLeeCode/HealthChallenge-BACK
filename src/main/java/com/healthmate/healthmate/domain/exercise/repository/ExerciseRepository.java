package com.healthmate.healthmate.domain.exercise.repository;

import com.healthmate.healthmate.domain.exercise.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByNameContainingIgnoreCase(String name);
    List<Exercise> findByCategory(String category);
    List<Exercise> findByDifficulty(String difficulty);
    
    @Query("SELECT e FROM Exercise e WHERE e.name LIKE %:keyword% OR e.description LIKE %:keyword% OR e.category LIKE %:keyword%")
    List<Exercise> searchByKeyword(@Param("keyword") String keyword);
}

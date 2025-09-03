package com.healthmate.healthmate.domain.exercise.repository;

import com.healthmate.healthmate.domain.exercise.entity.Difficulty;
import com.healthmate.healthmate.domain.exercise.entity.Exercise;
import com.healthmate.healthmate.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByNameEnContainingIgnoreCaseOrNameKoContainingIgnoreCase(String nameEn, String nameKo);
    List<Exercise> findByCategory(String category);
    List<Exercise> findByDifficulty(Difficulty difficulty);
    List<Exercise> findByCreatedByOrCreatedByRole(User createdBy, com.healthmate.healthmate.domain.user.entity.UserRole role);
    
    @Query("SELECT e FROM Exercise e WHERE (e.nameEn LIKE %:keyword% OR e.nameKo LIKE %:keyword% OR e.description LIKE %:keyword% OR e.category LIKE %:keyword%) AND (e.createdBy = :user OR e.createdBy.role = 'ADMIN')")
    List<Exercise> searchByKeyword(@Param("keyword") String keyword, @Param("user") User user);
}

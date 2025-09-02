package com.healthmate.healthmate.domain.routineexercise.repository;

import com.healthmate.healthmate.domain.routine.entity.Routine;
import com.healthmate.healthmate.domain.routineexercise.entity.RoutineExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoutineExerciseRepository extends JpaRepository<RoutineExercise, Long> {
    List<RoutineExercise> findByRoutineOrderByOrderNumberAsc(Routine routine);
    
    Optional<RoutineExercise> findByIdAndRoutine(Long id, Routine routine);
    
    @Modifying
    @Query("DELETE FROM RoutineExercise re WHERE re.routine = :routine")
    void deleteByRoutine(@Param("routine") Routine routine);
    
    @Query("SELECT MAX(re.orderNumber) FROM RoutineExercise re WHERE re.routine = :routine")
    Integer findMaxOrderNumberByRoutine(@Param("routine") Routine routine);
}

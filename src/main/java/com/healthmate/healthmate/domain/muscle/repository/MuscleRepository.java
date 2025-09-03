package com.healthmate.healthmate.domain.muscle.repository;

import com.healthmate.healthmate.domain.muscle.entity.Muscle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MuscleRepository extends JpaRepository<Muscle, Long> {
}



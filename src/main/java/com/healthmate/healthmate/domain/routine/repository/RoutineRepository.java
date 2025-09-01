package com.healthmate.healthmate.domain.routine.repository;

import com.healthmate.healthmate.domain.routine.entity.Routine;
import com.healthmate.healthmate.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine> findByUser(User user);
    Optional<Routine> findByIdAndUser(Long id, User user);
}

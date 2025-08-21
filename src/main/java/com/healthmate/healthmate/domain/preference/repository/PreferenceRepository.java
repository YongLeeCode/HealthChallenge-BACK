package com.healthmate.healthmate.domain.preference.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.healthmate.healthmate.domain.preference.entity.Preference;
import java.util.List;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {
	List<Preference> findAllByExerciseId(Integer exerciseId);
}

package com.healthmate.healthmate.domain.muscle.service;

import com.healthmate.healthmate.domain.muscle.dto.MuscleDtos.*;

import java.util.List;

public interface MuscleService {
	MuscleResponse createMuscle(CreateMuscleRequest request);
	MuscleResponse updateMuscle(Long muscleId, UpdateMuscleRequest request);
	void deleteMuscle(Long muscleId);
	List<MuscleResponse> getAll();
}



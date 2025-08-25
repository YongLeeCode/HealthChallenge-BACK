package com.healthmate.healthmate.domain.exercise.service;

import com.healthmate.healthmate.domain.exercise.dto.request.AddExerciseRequestDto;
import com.healthmate.healthmate.domain.exercise.dto.request.UpdateExerciseRequestDto;
import com.healthmate.healthmate.domain.exercise.dto.response.ExerciseResponseDto;
import java.util.List;

public interface ExerciseService {
	Long add(AddExerciseRequestDto req, Long currentUserId, boolean isAdmin);
	void update(Long id, UpdateExerciseRequestDto req, Long currentUserId, boolean isAdmin);
	void delete(Long id, Long currentUserId, boolean isAdmin);
	ExerciseResponseDto get(Long id);
	List<ExerciseResponseDto> list();
}



package com.healthmate.healthmate.domain.muscle.service;

import com.healthmate.healthmate.domain.exercise.entity.Exercise;
import com.healthmate.healthmate.domain.exercise.repository.ExerciseRepository;
import com.healthmate.healthmate.domain.muscle.dto.MuscleDtos.*;
import com.healthmate.healthmate.domain.muscle.entity.Muscle;
import com.healthmate.healthmate.domain.muscle.repository.MuscleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MuscleServiceImpl implements MuscleService {

	private final MuscleRepository muscleRepository;
	private final ExerciseRepository exerciseRepository;

	@Override
	@Transactional
	public MuscleResponse createMuscle(CreateMuscleRequest request) {
		Muscle muscle = Muscle.builder()
				.korName(request.korName())
				.engName(request.engName())
				.category(request.category())
				.build();
		Muscle saved = muscleRepository.save(muscle);
		return convertToResponse(saved);
	}

	@Override
	@Transactional
	public MuscleResponse updateMuscle(Long muscleId, UpdateMuscleRequest request) {
		Muscle muscle = muscleRepository.findById(muscleId)
			.orElseThrow(() -> new IllegalArgumentException("근육 정보를 찾을 수 없습니다."));
		muscle.setKorName(request.korName());
		muscle.setEngName(request.engName());
		muscle.setCategory(request.category());
		return convertToResponse(muscle);
	}

	@Override
	@Transactional
	public void deleteMuscle(Long muscleId) {
		Muscle muscle = muscleRepository.findById(muscleId)
			.orElseThrow(() -> new IllegalArgumentException("근육 정보를 찾을 수 없습니다."));
		muscleRepository.delete(muscle);
	}

	@Override
	public List<MuscleResponse> getAll() {
		return muscleRepository.findAll().stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	private MuscleResponse convertToResponse(Muscle muscle) {
		return new MuscleResponse(
				muscle.getId(),
				muscle.getKorName(),
				muscle.getEngName(),
				muscle.getCategory()
		);
	}
}



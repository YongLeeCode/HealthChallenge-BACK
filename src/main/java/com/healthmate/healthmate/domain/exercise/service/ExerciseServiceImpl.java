package com.healthmate.healthmate.domain.exercise.service;

import com.healthmate.healthmate.domain.exercise.dto.request.AddExerciseRequestDto;
import com.healthmate.healthmate.domain.exercise.dto.request.UpdateExerciseRequestDto;
import com.healthmate.healthmate.domain.exercise.dto.response.ExerciseResponseDto;
import com.healthmate.healthmate.domain.exercise.entity.Exercise;
import com.healthmate.healthmate.domain.exercise.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
	private final ExerciseRepository exerciseRepository;

	@Override
	public Long add(AddExerciseRequestDto req) {
		Exercise e = new Exercise(
			req.nameEn(), req.nameKo(), req.difficulty(),
			req.equipment(), req.howTo(), req.targetMuscles(), req.cautions()
		);
		return exerciseRepository.save(e).getId();
	}

	@Override
	public void update(Long id, UpdateExerciseRequestDto req) {
		Exercise e = exerciseRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + id));
		if (req.nameEn() != null) e.setNameEn(req.nameEn());
		if (req.nameKo() != null) e.setNameKo(req.nameKo());
		if (req.difficulty() != null) e.setDifficulty(req.difficulty());
		if (req.equipment() != null) e.setEquipment(req.equipment());
		if (req.howTo() != null) e.setHowTo(req.howTo());
		if (req.targetMuscles() != null) e.setTargetMuscles(req.targetMuscles());
		if (req.cautions() != null) e.setCautions(req.cautions());
		exerciseRepository.save(e);
	}

	@Override
	public void delete(Long id) {
		if (!exerciseRepository.existsById(id)) {
			throw new IllegalArgumentException("Exercise not found: " + id);
		}
		exerciseRepository.deleteById(id);
	}

	@Override
	public ExerciseResponseDto get(Long id) {
		Exercise e = exerciseRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + id));
		return ExerciseResponseDto.from(e);
	}

	@Override
	public List<ExerciseResponseDto> list() {
		return exerciseRepository.findAll().stream()
			.map(ExerciseResponseDto::from)
			.collect(Collectors.toList());
	}
}



package com.healthmate.healthmate.domain.exercise.service;

import com.healthmate.healthmate.domain.exercise.dto.request.AddExerciseRequestDto;
import com.healthmate.healthmate.domain.exercise.dto.request.UpdateExerciseRequestDto;
import com.healthmate.healthmate.domain.exercise.dto.response.ExerciseResponseDto;
import com.healthmate.healthmate.domain.exercise.entity.Exercise;
import com.healthmate.healthmate.domain.exercise.repository.ExerciseRepository;
import com.healthmate.healthmate.domain.user.entity.User;
import com.healthmate.healthmate.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
	private final ExerciseRepository exerciseRepository;
	private final UserRepository userRepository;

	@Override
	public Long add(AddExerciseRequestDto req, Long currentUserId, boolean isAdmin) {
		Exercise e = new Exercise(
			req.nameEn(), req.nameKo(), req.difficulty(),
			req.equipment(), req.howTo(), req.targetMuscles(), req.cautions()
		);
		User creator = userRepository.findById(currentUserId).orElseThrow();
		e.setCreator(creator);
		e.setOfficial(isAdmin);
		return exerciseRepository.save(e).getId();
	}

	@Override
	public void update(Long id, UpdateExerciseRequestDto req, Long currentUserId, boolean isAdmin) {
		Exercise e = exerciseRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + id));
		if (!isAdmin) {
			Long creatorId = e.getCreator() != null ? e.getCreator().getId() : null;
			if (creatorId == null || !creatorId.equals(currentUserId)) {
				throw new AccessDeniedException("You can only modify exercises you created.");
			}
		}
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
	public void delete(Long id, Long currentUserId, boolean isAdmin) {
		Exercise e = exerciseRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + id));
		if (!isAdmin) {
			Long creatorId = e.getCreator() != null ? e.getCreator().getId() : null;
			if (creatorId == null || !creatorId.equals(currentUserId)) {
				throw new AccessDeniedException("You can only delete exercises you created.");
			}
		}
		exerciseRepository.delete(e);
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



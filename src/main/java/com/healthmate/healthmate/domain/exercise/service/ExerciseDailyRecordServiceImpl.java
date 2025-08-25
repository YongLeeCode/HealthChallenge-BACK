package com.healthmate.healthmate.domain.exercise.service;

import com.healthmate.healthmate.domain.exercise.dto.request.AddExerciseDailyRecordRequestDto;
import com.healthmate.healthmate.domain.exercise.dto.request.UpdateExerciseDailyRecordRequestDto;
import com.healthmate.healthmate.domain.exercise.dto.response.ExerciseDailyRecordResponseDto;
import com.healthmate.healthmate.domain.exercise.entity.Exercise;
import com.healthmate.healthmate.domain.exercise.entity.ExerciseDailyRecord;
import com.healthmate.healthmate.domain.exercise.repository.ExerciseDailyRecordRepository;
import com.healthmate.healthmate.domain.exercise.repository.ExerciseRepository;
import com.healthmate.healthmate.domain.user.entity.User;
import com.healthmate.healthmate.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseDailyRecordServiceImpl implements ExerciseDailyRecordService {
    private final ExerciseDailyRecordRepository dailyRecordRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    @Override
    public Long upsert(AddExerciseDailyRecordRequestDto req) {
        User user = userRepository.findById(req.getUserId()).orElseThrow();
        Exercise representative = null;
        if (req.getRepresentativeExerciseId() != null) {
            representative = exerciseRepository.findById(req.getRepresentativeExerciseId()).orElseThrow();
        }

        LocalDate date = req.getDate() != null ? req.getDate() : LocalDate.now();
        Optional<ExerciseDailyRecord> existingOpt = dailyRecordRepository.findByUserAndDate(user, date);
        if (existingOpt.isPresent()) {
            ExerciseDailyRecord r = existingOpt.get();
            if (req.getTotalDurationSeconds() != null) r.setTotalDurationSeconds(req.getTotalDurationSeconds());
            if (req.getTotalSets() != null) r.setTotalSets(req.getTotalSets());
            if (req.getPerceivedDifficulty() != null) r.setPerceivedDifficulty(req.getPerceivedDifficulty());
            if (req.getSatisfaction() != null) r.setSatisfaction(req.getSatisfaction());
            if (representative != null) r.setRepresentativeExercise(representative);
            if (req.getNotes() != null) r.setNotes(req.getNotes());
            return r.getId();
        }

        ExerciseDailyRecord created = new ExerciseDailyRecord(
                user,
                date,
                req.getTotalDurationSeconds(),
                req.getTotalSets(),
                req.getPerceivedDifficulty(),
                req.getSatisfaction(),
                representative,
                req.getNotes()
        );
        return dailyRecordRepository.save(created).getId();
    }

    @Override
    public void update(Long id, UpdateExerciseDailyRecordRequestDto req) {
        ExerciseDailyRecord r = dailyRecordRepository.findById(id).orElseThrow();
        if (req.getTotalDurationSeconds() != null) r.setTotalDurationSeconds(req.getTotalDurationSeconds());
        if (req.getTotalSets() != null) r.setTotalSets(req.getTotalSets());
        if (req.getPerceivedDifficulty() != null) r.setPerceivedDifficulty(req.getPerceivedDifficulty());
        if (req.getSatisfaction() != null) r.setSatisfaction(req.getSatisfaction());
        if (req.getRepresentativeExerciseId() != null) {
            Exercise representative = exerciseRepository.findById(req.getRepresentativeExerciseId()).orElseThrow();
            r.setRepresentativeExercise(representative);
        }
        if (req.getNotes() != null) r.setNotes(req.getNotes());
    }

    @Override
    public void delete(Long id) {
        dailyRecordRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseDailyRecordResponseDto get(Long id) {
        ExerciseDailyRecord r = dailyRecordRepository.findById(id).orElseThrow();
        return toDto(r);
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseDailyRecordResponseDto getByUserAndDate(Long userId, LocalDate date) {
        User user = userRepository.findById(userId).orElseThrow();
        ExerciseDailyRecord r = dailyRecordRepository.findByUserAndDate(user, date).orElseThrow();
        return toDto(r);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseDailyRecordResponseDto> listByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return dailyRecordRepository.findByUserOrderByDateDesc(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ExerciseDailyRecordResponseDto toDto(ExerciseDailyRecord r) {
        Long representativeId = r.getRepresentativeExercise() != null ? r.getRepresentativeExercise().getId() : null;
        return new ExerciseDailyRecordResponseDto(
                r.getId(),
                r.getUser().getId(),
                r.getDate(),
                r.getTotalDurationSeconds(),
                r.getTotalSets(),
                r.getPerceivedDifficulty(),
                r.getSatisfaction(),
                representativeId,
                r.getNotes()
        );
    }
}



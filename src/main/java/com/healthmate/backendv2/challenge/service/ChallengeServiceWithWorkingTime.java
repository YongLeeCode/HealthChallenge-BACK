package com.healthmate.backendv2.challenge.service;

import com.healthmate.backendv2.challenge.dto.WorkingTimeSubmissionRequest;
import com.healthmate.backendv2.challenge.dto.WeeklyChallengeResponse;
import com.healthmate.backendv2.challenge.repository.ChallengeRedisRepository;
import com.healthmate.backendv2.exercise.MeasurementType;
import com.healthmate.backendv2.exercise.service.ExerciseService;
import com.healthmate.backendv2.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeServiceWithWorkingTime implements ChallengeService {
    
    private final ChallengeRedisRepository challengeRedisRepository;
    private final ExerciseService exerciseService;
    private final UserService userService;
    
    // 지속성 챌린지 점수 계산 상수
    private static final int BASE_POINTS_PER_MINUTE = 10; // 분당 기본 점수
    private static final int INTENSITY_MULTIPLIER_BASE = 5; // 강도 배수 기본값
    private static final int DURATION_BONUS_THRESHOLD = 30; // 30분 이상 지속시 보너스
    private static final int DURATION_BONUS_POINTS = 50; // 지속 보너스 점수
    
    @Override
    public Integer calculatePoints(com.healthmate.backendv2.challenge.dto.ChallengeBatchSubmissionRequest.ExerciseSubmission exerciseSubmission) {
        if (exerciseSubmission instanceof com.healthmate.backendv2.challenge.dto.ChallengeBatchSubmissionRequest.WorkingTimeExerciseSubmission workingTimeSubmission) {
            return calculateWorkingTimePoints(workingTimeSubmission.getDurationTimeSeconds());
        }
        throw new IllegalArgumentException("지원하지 않는 운동 제출 타입입니다: " + exerciseSubmission.getClass().getSimpleName());
    }
    
    @Override
    public MeasurementType getSupportedMeasurementType() {
        return MeasurementType.WORKING_TIME;
    }

    /**
     * 지속시간 기반 점수 계산 로직
     */
    private int calculateWorkingTimePoints(Integer durationTimeSeconds) {
        // 초를 분으로 변환
        int durationMinutes = durationTimeSeconds / 60;
        
        // 기본 점수: 시간 * 분당 기본 점수
        int basePoints = durationMinutes * BASE_POINTS_PER_MINUTE;
        
        // 지속 보너스 (30분 이상)
        int durationBonus = durationMinutes >= DURATION_BONUS_THRESHOLD ? 
                DURATION_BONUS_POINTS : 0;
        
        return (int) Math.round((basePoints + durationBonus) * 1.2); // 작업시간 1.2배
    }
    
    /**
     * 주어진 날짜가 속한 주의 시작일 (월요일) 계산
     */
    private LocalDate getWeekStart(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        return date.minusDays(dayOfWeek - 1);
    }
}

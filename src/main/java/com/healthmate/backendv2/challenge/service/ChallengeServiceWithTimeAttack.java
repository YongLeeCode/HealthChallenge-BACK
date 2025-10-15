package com.healthmate.backendv2.challenge.service;

import com.healthmate.backendv2.exercise.MeasurementType;
import com.healthmate.backendv2.exercise.service.ExerciseService;
import com.healthmate.backendv2.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeServiceWithTimeAttack implements ChallengeService {
    private final ExerciseService exerciseService;
    private final UserService userService;
    
    // 민첩성 챌린지 점수 계산 상수
    private static final int BASE_POINTS_PER_SECOND = 50; // 초당 기본 점수
    private static final int BONUS_POINTS_FOR_FAST_COMPLETION = 100; // 빠른 완료 보너스
    private static final int FAST_COMPLETION_THRESHOLD = 30; // 30초 이내 완료시 보너스
    
    @Override
    public Integer calculatePoints(com.healthmate.backendv2.challenge.dto.ChallengeBatchSubmissionRequest.ExerciseSubmission exerciseSubmission) {
        if (exerciseSubmission instanceof com.healthmate.backendv2.challenge.dto.ChallengeBatchSubmissionRequest.TimeAttackExerciseSubmission timeAttackSubmission) {
            return calculateTimeAttackPoints(timeAttackSubmission.getCompletionTimeSeconds());
        }
        throw new IllegalArgumentException("지원하지 않는 운동 제출 타입입니다: " + exerciseSubmission.getClass().getSimpleName());
    }
    
    @Override
    public MeasurementType getSupportedMeasurementType() {
        return MeasurementType.TIME_ATTACK;
    }
    
    /**
     * 타임어택 점수 계산 로직 (빠를수록 높은 점수)
     */
    private int calculateTimeAttackPoints(Integer completionTimeSeconds) {
        // 기본 점수
        int basePoints = 100;
        
        // 시간 보너스: 빠를수록 높은 점수 (최대 60초 기준)
        int timeBonus = Math.max(0, 60 - completionTimeSeconds) * 5;
        
        // 빠른 완료 보너스 (30초 이내)
        int fastCompletionBonus = completionTimeSeconds <= FAST_COMPLETION_THRESHOLD ? 
                BONUS_POINTS_FOR_FAST_COMPLETION : 0;
        
        return basePoints + timeBonus + fastCompletionBonus;
    }
}

package com.healthmate.backendv2.challenge.service;

import com.healthmate.backendv2.challenge.dto.ChallengeSubmissionResponse;
import com.healthmate.backendv2.challenge.dto.WeightSubmissionRequest;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeServiceWithWeight implements ChallengeService {
    
    private final ChallengeRedisRepository challengeRedisRepository;
    private final ExerciseService exerciseService;
    private final UserService userService;
    
    // 근력 챌린지 점수 계산 상수
    private static final int BASE_POINTS_PER_KG = 10; // kg당 기본 점수
    private static final int BASE_POINTS_PER_REP = 5; // 반복당 기본 점수
    private static final int BASE_POINTS_PER_SET = 20; // 세트당 기본 점수
    private static final double HEAVY_WEIGHT_MULTIPLIER = 1.5; // 무거운 무게 배수 (50kg 이상)
    
    @Override
    public Integer calculatePoints(com.healthmate.backendv2.challenge.dto.ChallengeBatchSubmissionRequest.ExerciseSubmission exerciseSubmission) {
        if (exerciseSubmission instanceof com.healthmate.backendv2.challenge.dto.ChallengeBatchSubmissionRequest.WeightExerciseSubmission weightSubmission) {
            return calculateWeightPoints(weightSubmission.getMaxWeightKg(), weightSubmission.getCounts());
        }
        throw new IllegalArgumentException("지원하지 않는 운동 제출 타입입니다: " + exerciseSubmission.getClass().getSimpleName());
    }
    
    @Override
    public MeasurementType getSupportedMeasurementType() {
        return MeasurementType.WEIGHT;
    }

    /**
     * 무게 기반 점수 계산 로직
     */
    private int calculateWeightPoints(Double maxWeightKg, Integer counts) {
        // 기본 점수: 무게 * 횟수
        int basePoints = (int) (maxWeightKg * counts * BASE_POINTS_PER_KG);
        
        // 횟수 보너스
        int countBonus = counts * BASE_POINTS_PER_REP;
        
        // 무거운 무게 보너스 (50kg 이상)
        double weightMultiplier = maxWeightKg >= 50.0 ? HEAVY_WEIGHT_MULTIPLIER : 1.0;
        
        return (int) Math.round((basePoints + countBonus) * weightMultiplier);
    }
}

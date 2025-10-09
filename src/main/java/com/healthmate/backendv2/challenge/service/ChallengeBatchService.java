package com.healthmate.backendv2.challenge.service;

import com.healthmate.backendv2.challenge.dto.ChallengeBatchSubmissionRequest;
import com.healthmate.backendv2.challenge.dto.ChallengeBatchSubmissionResponse;
import com.healthmate.backendv2.challenge.dto.ChallengeSubmissionResponse;
import com.healthmate.backendv2.challenge.dto.TimeAttackSubmissionRequest;
import com.healthmate.backendv2.challenge.dto.WeightSubmissionRequest;
import com.healthmate.backendv2.challenge.dto.WorkingTimeSubmissionRequest;
import com.healthmate.backendv2.challenge.repository.ChallengeRedisRepository;
import com.healthmate.backendv2.exercise.ExerciseType;
import com.healthmate.backendv2.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeBatchService {
    
    private final ChallengeRedisRepository challengeRedisRepository;
    private final ExerciseService exerciseService;
    private final ChallengeServiceWithTimeAttack timeAttackService;
    private final ChallengeServiceWithWeight weightService;
    private final ChallengeServiceWithWorkingTime workingTimeService;
    
    /**
     * 챌린지 전체 제출 및 배치 처리
     */
    @Transactional
    public ChallengeBatchSubmissionResponse submitChallengeBatch(Long userId, ChallengeBatchSubmissionRequest request) {
        log.info("User {} submitting challenge batch: {}", userId, request.getChallengeId());
        
        List<ChallengeBatchSubmissionResponse.ExerciseSubmissionResult> exerciseResults = new ArrayList<>();
        int totalPointsEarned = 0;
        
        // 각 운동별로 점수 계산 (트랜잭션 없이)
        for (ChallengeBatchSubmissionRequest.ExerciseSubmission exerciseSubmission : request.getExercises()) {
            try {
                ChallengeBatchSubmissionResponse.ExerciseSubmissionResult result = 
                    processExerciseSubmission(userId, exerciseSubmission);
                exerciseResults.add(result);
                
                if ("SUCCESS".equals(result.getStatus())) {
                    totalPointsEarned += result.getPointsEarned();
                }
            } catch (Exception e) {
                log.error("Error processing exercise {}: {}", exerciseSubmission.getExerciseId(), e.getMessage());
                exerciseResults.add(ChallengeBatchSubmissionResponse.ExerciseSubmissionResult.builder()
                        .exerciseId(exerciseSubmission.getExerciseId())
                        .exerciseName("Unknown")
                        .exerciseType("Unknown")
                        .pointsEarned(0)
                        .status("FAILED")
                        .errorMessage(e.getMessage())
                        .build());
            }
        }
        
        // 현재 주간 챌린지 키
        LocalDate currentWeek = getCurrentWeekStart();
        String challengeKey = challengeRedisRepository.getWeeklyChallengeKey(currentWeek);
        
        // 기존 포인트 조회
        Double currentPoints = challengeRedisRepository.getUserPoints(challengeKey, userId);
        int totalPoints = (currentPoints != null ? currentPoints.intValue() : 0) + totalPointsEarned;
        
        // Redis에 포인트 업데이트 (배치 처리)
        challengeRedisRepository.addUserPoints(challengeKey, userId, totalPoints);
        
        // 사용자별 포인트 히스토리 저장
        challengeRedisRepository.saveUserPointsHistory(userId, currentWeek, totalPoints);
        
        // 현재 순위 조회
        Long currentRank = challengeRedisRepository.getUserRank(challengeKey, userId);
        
        log.info("User {} completed challenge batch with {} total points, rank: {}", 
                userId, totalPoints, currentRank);
        
        return ChallengeBatchSubmissionResponse.builder()
                .challengeId(request.getChallengeId())
                .totalPointsEarned(totalPointsEarned)
                .totalPoints(totalPoints)
                .currentRank(currentRank != null ? currentRank.intValue() : 0)
                .submittedAt(LocalDateTime.now())
                .exerciseResults(exerciseResults)
                .notes(request.getNotes())
                .build();
    }
    
    /**
     * 개별 운동 제출 처리 (점수 계산만, 트랜잭션 없이)
     */
    private ChallengeBatchSubmissionResponse.ExerciseSubmissionResult processExerciseSubmission(
            Long userId, ChallengeBatchSubmissionRequest.ExerciseSubmission exerciseSubmission) {
        
        // 운동 정보 조회
        var exerciseResponse = exerciseService.getById(exerciseSubmission.getExerciseId());
        if (exerciseResponse == null) {
            throw new IllegalArgumentException("존재하지 않는 운동입니다: " + exerciseSubmission.getExerciseId());
        }
        
        try {
            // 운동 타입에 따른 점수 계산 (트랜잭션 없이)
            int pointsEarned = calculatePointsByExerciseType(exerciseResponse, exerciseSubmission);
            
            return ChallengeBatchSubmissionResponse.ExerciseSubmissionResult.builder()
                    .exerciseId(exerciseSubmission.getExerciseId())
                    .exerciseName(exerciseResponse.getName())
                    .exerciseType(String.valueOf(exerciseResponse.getExerciseType()))
                    .pointsEarned(pointsEarned)
                    .status("SUCCESS")
                    .errorMessage(null)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error calculating points for exercise {}: {}", exerciseSubmission.getExerciseId(), e.getMessage());
            return ChallengeBatchSubmissionResponse.ExerciseSubmissionResult.builder()
                    .exerciseId(exerciseSubmission.getExerciseId())
                    .exerciseName(exerciseResponse.getName())
                    .exerciseType(String.valueOf(exerciseResponse.getExerciseType()))
                    .pointsEarned(0)
                    .status("FAILED")
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
    
    /**
     * 운동 타입에 따른 점수 계산
     */
    private int calculatePointsByExerciseType(
            com.healthmate.backendv2.exercise.dto.ExerciseResponse exercise,
            ChallengeBatchSubmissionRequest.ExerciseSubmission submission) {
        
        switch (exercise.getExerciseType()) {
            case CARDIO:
                return calculateCardioPoints(submission);
            case STRENGTH:
                return calculateStrengthPoints(submission);
            default:
                return calculateTimeAttackPoints(submission);
        }
    }
    
    /**
     * 카디오 운동 점수 계산 (지속시간 기반)
     */
    private int calculateCardioPoints(ChallengeBatchSubmissionRequest.ExerciseSubmission submission) {
        if (submission.getDurationMinutes() == null || submission.getIntensity() == null) {
            throw new IllegalArgumentException("카디오 운동에는 지속시간과 강도가 필요합니다");
        }
        
        int basePoints = submission.getDurationMinutes() * 10; // 분당 10점
        int intensityBonus = submission.getIntensity() * 5; // 강도당 5점
        int durationBonus = submission.getDurationMinutes() >= 30 ? 50 : 0; // 30분 이상 보너스
        
        return (int) Math.round((basePoints + intensityBonus + durationBonus) * 1.5); // 카디오 1.5배
    }
    
    /**
     * 근력 운동 점수 계산 (무게 기반)
     */
    private int calculateStrengthPoints(ChallengeBatchSubmissionRequest.ExerciseSubmission submission) {
        if (submission.getWeightKg() == null || submission.getSets() == null || submission.getReps() == null) {
            throw new IllegalArgumentException("근력 운동에는 무게, 세트, 반복 횟수가 필요합니다");
        }
        
        int basePoints = (int) (submission.getWeightKg() * submission.getSets() * submission.getReps() * 10);
        int setBonus = submission.getSets() * 20;
        int repBonus = submission.getReps() * 5;
        
        double weightMultiplier = submission.getWeightKg() >= 50.0 ? 1.2 : 1.0;
        
        return (int) Math.round((basePoints + setBonus + repBonus) * weightMultiplier);
    }
    
    /**
     * 타임어택 운동 점수 계산
     */
    private int calculateTimeAttackPoints(ChallengeBatchSubmissionRequest.ExerciseSubmission submission) {
        if (submission.getCompletionTimeSeconds() == null || submission.getTargetCount() == null) {
            throw new IllegalArgumentException("타임어택 운동에는 완료시간과 목표횟수가 필요합니다");
        }
        
        int basePoints = submission.getTargetCount() * 50; // 횟수당 50점
        int timeBonus = Math.max(0, 30 - submission.getCompletionTimeSeconds()) * 10; // 빠른 완료 보너스
        int fastCompletionBonus = submission.getCompletionTimeSeconds() <= 30 ? 100 : 0; // 30초 이내 보너스
        
        return basePoints + timeBonus + fastCompletionBonus;
    }
    
    /**
     * 현재 주의 시작일 (월요일) 계산
     */
    private LocalDate getCurrentWeekStart() {
        LocalDate now = LocalDate.now();
        int dayOfWeek = now.getDayOfWeek().getValue();
        return now.minusDays(dayOfWeek - 1);
    }
}

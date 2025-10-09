package com.healthmate.backendv2.challenge.service;

import com.healthmate.backendv2.challenge.dto.ChallengeBatchSubmissionRequest;
import com.healthmate.backendv2.challenge.dto.ChallengeBatchSubmissionResponse;
import com.healthmate.backendv2.challenge.dto.ChallengeSubmissionResponse;
import com.healthmate.backendv2.challenge.dto.TimeAttackSubmissionRequest;
import com.healthmate.backendv2.challenge.dto.WeightSubmissionRequest;
import com.healthmate.backendv2.challenge.dto.WorkingTimeSubmissionRequest;
import com.healthmate.backendv2.challenge.repository.ChallengeRedisRepository;
import com.healthmate.backendv2.exercise.ExerciseType;
import com.healthmate.backendv2.exercise.MeasurementType;
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
        
        // 각 운동별로 점수 계산 및 최고 기록 갱신 (트랜잭션 없이)
        for (ChallengeBatchSubmissionRequest.ExerciseSubmission exerciseSubmission : request.getExercises()) {
            try {
                ChallengeBatchSubmissionResponse.ExerciseSubmissionResult result = 
                    processExerciseSubmissionWithRecordUpdate(userId, exerciseSubmission);
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
        
        // 최고 기록 갱신 (배치에서 얻은 총 점수가 더 높을 때만 업데이트)
        boolean isNewRecord = currentPoints == null || totalPointsEarned > currentPoints.intValue();
        int finalPoints = isNewRecord ? totalPointsEarned : (currentPoints != null ? currentPoints.intValue() : 0);
        
        if (isNewRecord) {
            challengeRedisRepository.updateUserPointsIfHigher(challengeKey, userId, totalPointsEarned);
            challengeRedisRepository.saveUserPointsHistory(userId, currentWeek, totalPointsEarned);
        }
        
        // 현재 순위 조회
        Long currentRank = challengeRedisRepository.getUserRank(challengeKey, userId);
        
        log.info("User {} completed challenge batch with {} total points, final: {}, rank: {}, newRecord: {}", 
                userId, totalPointsEarned, finalPoints, currentRank, isNewRecord);
        
        return ChallengeBatchSubmissionResponse.builder()
                .challengeId(request.getChallengeId())
                .totalPointsEarned(totalPointsEarned)
                .totalPoints(finalPoints)
                .currentRank(currentRank != null ? currentRank.intValue() : 0)
                .submittedAt(LocalDateTime.now())
                .exerciseResults(exerciseResults)
                .notes(request.getNotes())
                .build();
    }
    
    /**
     * 개별 운동 제출 처리 (점수 계산 및 최고 기록 갱신, 트랜잭션 없이)
     */
    private ChallengeBatchSubmissionResponse.ExerciseSubmissionResult processExerciseSubmissionWithRecordUpdate(
            Long userId, ChallengeBatchSubmissionRequest.ExerciseSubmission exerciseSubmission) {
        
        // 운동 정보 조회
        var exerciseResponse = exerciseService.getById(exerciseSubmission.getExerciseId());
        if (exerciseResponse == null) {
            throw new IllegalArgumentException("존재하지 않는 운동입니다: " + exerciseSubmission.getExerciseId());
        }
        
        try {
            // 운동 타입에 따른 점수 계산 (트랜잭션 없이)
            int pointsEarned = calculatePointsByExerciseType(exerciseResponse, exerciseSubmission);
            
            // 현재 주간 챌린지 키
            LocalDate currentWeek = getCurrentWeekStart();
            String challengeKey = challengeRedisRepository.getWeeklyChallengeKey(currentWeek);
            
            // 기존 포인트 조회
            Double currentPoints = challengeRedisRepository.getUserPoints(challengeKey, userId);
            
            // 최고 기록 갱신 (새 점수가 더 높을 때만 업데이트)
            boolean isNewRecord = currentPoints == null || pointsEarned > currentPoints.intValue();
            
            if (isNewRecord) {
                challengeRedisRepository.updateUserPointsIfHigher(challengeKey, userId, pointsEarned);
            }
            
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
     * 개별 운동 제출 처리 (점수 계산만, 트랜잭션 없이) - 기존 메서드
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
     * MeasurementType에 따른 점수 계산 (다형성 구현)
     */
    private int calculatePointsByExerciseType(
            com.healthmate.backendv2.exercise.dto.ExerciseResponse exercise,
            ChallengeBatchSubmissionRequest.ExerciseSubmission submission) {
        
        switch (exercise.getMeasurementType()) {
            case WORKING_TIME:
                return calculateWorkingTimePoints(submission);
            case WEIGHT:
                return calculateWeightPoints(submission);
            case TIME_ATTACK:
                return calculateTimeAttackPoints(submission);
            default:
                throw new IllegalArgumentException("지원하지 않는 측정 타입입니다: " + exercise.getMeasurementType());
        }
    }
    
    /**
     * WORKING_TIME 운동 점수 계산 (지속시간 기반)
     */
    private int calculateWorkingTimePoints(ChallengeBatchSubmissionRequest.ExerciseSubmission submission) {
        if (submission.getDurationTimeSeconds() == null) {
            throw new IllegalArgumentException("WORKING_TIME 운동에는 지속시간(durationTimeSeconds)이 필요합니다");
        }
        
        int durationMinutes = submission.getDurationTimeSeconds() / 60;
        int basePoints = durationMinutes * 10; // 분당 10점
        int durationBonus = durationMinutes >= 30 ? 50 : 0; // 30분 이상 보너스
        
        return (int) Math.round((basePoints + durationBonus) * 1.2); // 작업시간 1.2배
    }
    
    /**
     * WEIGHT 운동 점수 계산 (무게 기반)
     */
    private int calculateWeightPoints(ChallengeBatchSubmissionRequest.ExerciseSubmission submission) {
        if (submission.getMaxWeightKg() == null || submission.getCounts() == null) {
            throw new IllegalArgumentException("WEIGHT 운동에는 최대무게(maxWeightKg)와 횟수(counts)가 필요합니다");
        }
        
        int basePoints = (int) (submission.getMaxWeightKg() * submission.getCounts() * 10);
        int countBonus = submission.getCounts() * 5;
        
        double weightMultiplier = submission.getMaxWeightKg() >= 50.0 ? 1.2 : 1.0;
        
        return (int) Math.round((basePoints + countBonus) * weightMultiplier);
    }
    
    /**
     * TIME_ATTACK 운동 점수 계산
     */
    private int calculateTimeAttackPoints(ChallengeBatchSubmissionRequest.ExerciseSubmission submission) {
        if (submission.getCompletionTimeSeconds() == null) {
            throw new IllegalArgumentException("TIME_ATTACK 운동에는 완료시간(completionTimeSeconds)이 필요합니다");
        }
        
        int basePoints = 100; // 기본 점수
        int timeBonus = Math.max(0, 60 - submission.getCompletionTimeSeconds()) * 5; // 빠른 완료 보너스
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

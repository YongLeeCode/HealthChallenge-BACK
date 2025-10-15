package com.healthmate.backendv2.challenge.service;

import com.healthmate.backendv2.challenge.dto.ChallengeBatchSubmissionRequest;
import com.healthmate.backendv2.challenge.dto.ChallengeBatchSubmissionResponse;
import com.healthmate.backendv2.challenge.repository.ChallengeRedisRepository;
import com.healthmate.backendv2.exercise.dto.ExerciseResponse;
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
    private final ChallengeTemplateService challengeTemplateService;
    private final List<ChallengeService> challengeServices;
    
    /**
     * 챌린지 전체 제출 및 배치 처리
     */
    @Transactional
    public ChallengeBatchSubmissionResponse submitChallengeBatch(Long userId, ChallengeBatchSubmissionRequest request) {
        log.info("User {} submitting challenge batch: {}", userId, request.getChallengeId());
        
        // 운영진이 지정한 운동만 허용하는지 검증
        validateExerciseSubmission(request);
        
        List<ChallengeBatchSubmissionResponse.ExerciseSubmissionResult> exerciseResults = new ArrayList<>();
        int totalPointsEarned = 0;
        
        // 각 운동별로 점수 계산 및 최고 기록 갱신
        for (ChallengeBatchSubmissionRequest.ExerciseSubmission exerciseSubmission : request.getExercises()) {
            try {
                ChallengeBatchSubmissionResponse.ExerciseSubmissionResult result = 
                    processExerciseSubmissionWithRecordUpdate(exerciseSubmission);
                exerciseResults.add(result);
                
                if ("SUCCESS".equals(result.getStatus())) {
                    totalPointsEarned += result.getPointsEarned();
                }
            } catch (Exception e) {
                log.error("Error processing exercise {}: {}", exerciseSubmission.getExerciseId(), e.getMessage());
                exerciseResults.add(ChallengeBatchSubmissionResponse.ExerciseSubmissionResult.builder()
                        .exerciseId(exerciseSubmission.getExerciseId())
                        .exerciseName("unknown")
                        .exerciseType("unknown")
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
            ChallengeBatchSubmissionRequest.ExerciseSubmission exerciseSubmission) {
        
        // 운동 정보 조회
        var exerciseResponse = exerciseService.getById(exerciseSubmission.getExerciseId());
        if (exerciseResponse == null) {
            throw new IllegalArgumentException("존재하지 않는 운동입니다: " + exerciseSubmission.getExerciseId());
        }
        
        try {
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
     * MeasurementType에 따른 점수 계산 (Strategy 패턴 사용)
     */
    private int calculatePointsByExerciseType(
            ExerciseResponse exercise,
            ChallengeBatchSubmissionRequest.ExerciseSubmission submission) {

        // 해당 측정 타입을 지원하는 서비스 찾기
        ChallengeService challengeService = challengeServices.stream()
                .filter(service -> service.getSupportedMeasurementType() == submission.getType())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "지원하지 않는 측정 타입입니다: " + submission.getType()));

        // 해당 서비스로 점수 계산
        return challengeService.calculatePoints(submission);
    }

    
    /**
     * 운동 제출 검증 - 운영진이 지정한 운동만 허용
     */
    private void validateExerciseSubmission(ChallengeBatchSubmissionRequest request) {
        // 현재 활성화된 챌린지 템플릿이 있는지 확인
        List<Long> allowedExerciseIds = challengeTemplateService.getCurrentActiveExerciseIds();
        
        if (allowedExerciseIds.isEmpty()) {
            log.warn("현재 활성화된 챌린지 템플릿이 없습니다. 모든 운동을 허용합니다.");
            return; // 활성화된 템플릿이 없으면 기존 방식으로 동작
        }
        
        // 제출하려는 운동들이 허용된 운동 목록에 포함되는지 확인
        for (ChallengeBatchSubmissionRequest.ExerciseSubmission exerciseSubmission : request.getExercises()) {
            if (!allowedExerciseIds.contains(exerciseSubmission.getExerciseId())) {
                throw new IllegalArgumentException(
                    String.format("운동 ID %d는 현재 챌린지에서 허용되지 않습니다. 허용된 운동: %s", 
                        exerciseSubmission.getExerciseId(), allowedExerciseIds)
                );
            }
        }
        
        log.info("운동 제출 검증 통과: {} 개의 운동이 모두 허용된 운동 목록에 포함됨", request.getExercises().size());
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

package com.healthmate.backendv2.challenge.service;

import com.healthmate.backendv2.challenge.dto.ChallengeSubmissionResponse;
import com.healthmate.backendv2.challenge.dto.WeightSubmissionRequest;
import com.healthmate.backendv2.challenge.dto.WeeklyChallengeResponse;
import com.healthmate.backendv2.challenge.repository.ChallengeRedisRepository;
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
    
    /**
     * 무게 기반 운동 제출 및 점수 계산
     */
    @Transactional
    public ChallengeSubmissionResponse submitWeightExercise(Long userId, WeightSubmissionRequest request) {
        // 운동 정보 조회
        var exerciseResponse = exerciseService.getById(request.getExerciseId());
        if (exerciseResponse == null) {
            throw new IllegalArgumentException("존재하지 않는 운동입니다.");
        }
        
        // 사용자 정보 조회
        var userResponse = userService.getById(userId);
        if (userResponse == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        
        // 근력 점수 계산 (무게, 횟수 기반)
        int pointsEarned = calculateWeightPoints(
            request.getMaxWeightKg(),
            request.getCounts()
        );
        
        // 현재 주간 챌린지 키
        LocalDate currentWeek = getCurrentWeekStart();
        String challengeKey = challengeRedisRepository.getWeeklyChallengeKey(currentWeek);
        
        // 기존 포인트 조회
        Double currentPoints = challengeRedisRepository.getUserPoints(challengeKey, userId);
        
        // 최고 기록 갱신 (새 점수가 더 높을 때만 업데이트)
        boolean isNewRecord = currentPoints == null || pointsEarned > currentPoints.intValue();
        int finalPoints = isNewRecord ? pointsEarned : (currentPoints != null ? currentPoints.intValue() : 0);
        
        if (isNewRecord) {
            challengeRedisRepository.updateUserPointsIfHigher(challengeKey, userId, pointsEarned);
            challengeRedisRepository.saveUserPointsHistory(userId, currentWeek, pointsEarned);
        }
        
        // 현재 순위 조회
        Long currentRank = challengeRedisRepository.getUserRank(challengeKey, userId);
        
        log.info("User {} submitted weight exercise {} with {}kg, {} counts, earned {} points, final: {}, rank: {}, newRecord: {}", 
                userId, request.getExerciseId(), request.getMaxWeightKg(), request.getCounts(), 
                pointsEarned, finalPoints, currentRank, isNewRecord);
        
        return ChallengeSubmissionResponse.builder()
                .submissionId(System.currentTimeMillis())
                .exerciseId(request.getExerciseId())
                .exerciseName(exerciseResponse.getName())
                .durationMinutes(0) // 무게 운동은 시간이 아닌 세트/반복 기반
                .pointsEarned(pointsEarned)
                .totalPoints(finalPoints)
                .currentRank(currentRank != null ? currentRank.intValue() : 0)
                .submittedAt(LocalDateTime.now())
                .notes(request.getNotes())
                .build();
    }
    
    @Override
    public WeeklyChallengeResponse getCurrentWeeklyChallenge() {
        return getWeeklyChallenge(getCurrentWeekStart());
    }
    
    @Override
    public WeeklyChallengeResponse getWeeklyChallenge(LocalDate date) {
        LocalDate weekStart = getWeekStart(date);
        LocalDate weekEnd = weekStart.plusDays(6);
        
        String challengeId = weekStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String status = determineChallengeStatus(weekStart, weekEnd);
        
        return WeeklyChallengeResponse.builder()
                .challengeId(challengeId)
                .startDate(weekStart)
                .endDate(weekEnd)
                .status(status)
                .currentUserPoints(0) // TODO: 현재 사용자 포인트 조회
                .currentUserRank(0)   // TODO: 현재 사용자 순위 조회
                .exercises(getWeightChallengeExercises())
                .build();
    }
    
    @Override
    public Integer getUserCurrentPoints(Long userId) {
        LocalDate currentWeek = getCurrentWeekStart();
        String challengeKey = challengeRedisRepository.getWeeklyChallengeKey(currentWeek);
        Double points = challengeRedisRepository.getUserPoints(challengeKey, userId);
        return points != null ? points.intValue() : 0;
    }
    
    @Override
    public Integer getUserWeeklyPoints(Long userId, LocalDate date) {
        LocalDate weekStart = getWeekStart(date);
        String challengeKey = challengeRedisRepository.getWeeklyChallengeKey(weekStart);
        Double points = challengeRedisRepository.getUserPoints(challengeKey, userId);
        return points != null ? points.intValue() : 0;
    }
    
    @Override
    public String getChallengeType() {
        return "WEIGHT";
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
    
    /**
     * 현재 주의 시작일 (월요일) 계산
     */
    private LocalDate getCurrentWeekStart() {
        return getWeekStart(LocalDate.now());
    }
    
    /**
     * 주어진 날짜가 속한 주의 시작일 (월요일) 계산
     */
    private LocalDate getWeekStart(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        return date.minusDays(dayOfWeek - 1);
    }
    
    /**
     * 챌린지 상태 결정
     */
    private String determineChallengeStatus(LocalDate startDate, LocalDate endDate) {
        LocalDate now = LocalDate.now();
        if (now.isBefore(startDate)) {
            return "UPCOMING";
        } else if (now.isAfter(endDate)) {
            return "COMPLETED";
        } else {
            return "ACTIVE";
        }
    }
    
    /**
     * 무게 챌린지 운동 목록 생성 (근력 관련 운동들)
     */
    private List<WeeklyChallengeResponse.ChallengeExercise> getWeightChallengeExercises() {
        // 근력 관련 운동들만 필터링
        var strengthExercises = exerciseService.getByExerciseType(
            com.healthmate.backendv2.exercise.ExerciseType.STRENGTH
        );
        
        List<WeeklyChallengeResponse.ChallengeExercise> challengeExercises = new ArrayList<>();
        
        for (var exercise : strengthExercises) {
            challengeExercises.add(WeeklyChallengeResponse.ChallengeExercise.builder()
                    .exerciseId(exercise.getId())
                    .exerciseName(exercise.getName())
                    .description(exercise.getDescription())
                    .measurementType(String.valueOf(exercise.getMeasurementType()))
                    .muscleFocusArea(String.valueOf(exercise.getMuscleFocusArea()))
                    .exerciseType(String.valueOf(exercise.getExerciseType()))
                    .imageUrl(exercise.getImageUrl())
                    .targetSets(3) // 기본 목표 세트
                    .targetDurationMinutes(0) // 무게 운동은 시간이 아닌 세트/반복 기반
                    .pointsPerSet(BASE_POINTS_PER_SET)
                    .pointsPerMinute(0) // 무게 운동은 시간 기반이 아님
                    .build());
        }
        
        return challengeExercises;
    }
}

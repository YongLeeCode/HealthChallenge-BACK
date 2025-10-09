package com.healthmate.backendv2.challenge.service;

import com.healthmate.backendv2.challenge.dto.ChallengeSubmissionResponse;
import com.healthmate.backendv2.challenge.dto.TimeAttackSubmissionRequest;
import com.healthmate.backendv2.challenge.dto.WeeklyChallengeResponse;
import com.healthmate.backendv2.challenge.repository.ChallengeRedisRepository;
import com.healthmate.backendv2.exercise.MuscleFocusArea;
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
public class ChallengeServiceWithTimeAttack implements ChallengeService {
    
    private final ChallengeRedisRepository challengeRedisRepository;
    private final ExerciseService exerciseService;
    private final UserService userService;
    
    // 민첩성 챌린지 점수 계산 상수
    private static final int BASE_POINTS_PER_SECOND = 50; // 초당 기본 점수
    private static final int BONUS_POINTS_FOR_FAST_COMPLETION = 100; // 빠른 완료 보너스
    private static final int FAST_COMPLETION_THRESHOLD = 30; // 30초 이내 완료시 보너스
    
    /**
     * 타임어택 운동 제출 및 점수 계산
     */
    @Transactional
    public ChallengeSubmissionResponse submitTimeAttackExercise(Long userId, TimeAttackSubmissionRequest request) {
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
        
        // 민첩성 점수 계산 (빠를수록 높은 점수)
        int pointsEarned = calculateTimeAttackPoints(
            request.getCompletionTimeSeconds(), 
            request.getTargetCount()
        );
        
        // 현재 주간 챌린지 키
        LocalDate currentWeek = getCurrentWeekStart();
        String challengeKey = challengeRedisRepository.getWeeklyChallengeKey(currentWeek);
        
        // 기존 포인트 조회
        Double currentPoints = challengeRedisRepository.getUserPoints(challengeKey, userId);
        int totalPoints = (currentPoints != null ? currentPoints.intValue() : 0) + pointsEarned;
        
        // Redis에 포인트 업데이트
        challengeRedisRepository.addUserPoints(challengeKey, userId, totalPoints);
        
        // 사용자별 포인트 히스토리 저장
        challengeRedisRepository.saveUserPointsHistory(userId, currentWeek, totalPoints);
        
        // 현재 순위 조회
        Long currentRank = challengeRedisRepository.getUserRank(challengeKey, userId);
        
        log.info("User {} submitted time attack exercise {} completed in {} seconds, earned {} points, total: {}, rank: {}", 
                userId, request.getExerciseId(), request.getCompletionTimeSeconds(), 
                pointsEarned, totalPoints, currentRank);
        
        return ChallengeSubmissionResponse.builder()
                .submissionId(System.currentTimeMillis())
                .exerciseId(request.getExerciseId())
                .exerciseName(exerciseResponse.getName())
                .sets(request.getTargetCount())
                .durationMinutes(request.getCompletionTimeSeconds() / 60)
                .pointsEarned(pointsEarned)
                .totalPoints(totalPoints)
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
                .exercises(getTimeAttackChallengeExercises())
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
        return "TIME_ATTACK";
    }
    
    /**
     * 타임어택 점수 계산 로직 (빠를수록 높은 점수)
     */
    private int calculateTimeAttackPoints(Integer completionTimeSeconds, Integer targetCount) {
        // 기본 점수: 목표 횟수 * 초당 기본 점수
        int basePoints = targetCount * BASE_POINTS_PER_SECOND;
        
        // 시간 보너스: 빠를수록 높은 점수 (최대 30초 기준)
        int timeBonus = Math.max(0, FAST_COMPLETION_THRESHOLD - completionTimeSeconds) * 10;
        
        // 빠른 완료 보너스
        int fastCompletionBonus = completionTimeSeconds <= FAST_COMPLETION_THRESHOLD ? 
                BONUS_POINTS_FOR_FAST_COMPLETION : 0;
        
        return basePoints + timeBonus + fastCompletionBonus;
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
     * 타임어택 챌린지 운동 목록 생성 (민첩성 관련 운동들)
     */
    private List<WeeklyChallengeResponse.ChallengeExercise> getTimeAttackChallengeExercises() {
        // 민첩성 관련 운동들만 필터링
        var agilityExercises = exerciseService.getByMuscleFocusArea(
            MuscleFocusArea.LOWER_BODY
        );
        
        List<WeeklyChallengeResponse.ChallengeExercise> challengeExercises = new ArrayList<>();
        
        for (var exercise : agilityExercises) {
            challengeExercises.add(WeeklyChallengeResponse.ChallengeExercise.builder()
                    .exerciseId(exercise.getId())
                    .exerciseName(exercise.getName())
                    .description(exercise.getDescription())
                    .measurementType(String.valueOf(exercise.getMeasurementType()))
                    .muscleFocusArea(String.valueOf(MuscleFocusArea.LOWER_BODY))
                    .exerciseType(String.valueOf(exercise.getExerciseType()))
                    .imageUrl(exercise.getImageUrl())
                    .targetSets(30) // 기본 목표 횟수
                    .targetDurationMinutes(1) // 기본 목표 시간 (1분)
                    .pointsPerSet(BASE_POINTS_PER_SECOND)
                    .pointsPerMinute(0) // 타임어택은 시간이 아닌 완료 시간으로 점수 계산
                    .build());
        }
        
        return challengeExercises;
    }
}

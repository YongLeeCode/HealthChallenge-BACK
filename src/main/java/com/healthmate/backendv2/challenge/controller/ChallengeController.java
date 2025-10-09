package com.healthmate.backendv2.challenge.controller;

import com.healthmate.backendv2.challenge.dto.*;
import com.healthmate.backendv2.challenge.service.ChallengeBatchService;
import com.healthmate.backendv2.challenge.service.ChallengeService;
import com.healthmate.backendv2.challenge.service.ChallengeServiceWithTimeAttack;
import com.healthmate.backendv2.challenge.service.ChallengeServiceWithWeight;
import com.healthmate.backendv2.challenge.service.ChallengeServiceWithWorkingTime;
import com.healthmate.backendv2.challenge.service.LeaderboardService;
import com.healthmate.backendv2.auth.config.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {
    
    private final ChallengeServiceWithTimeAttack timeAttackService;
    private final ChallengeServiceWithWeight weightService;
    private final ChallengeServiceWithWorkingTime workingTimeService;
    private final LeaderboardService leaderboardService;
    private final ChallengeBatchService challengeBatchService;
    private final JwtUtils jwtUtils;
    
    /**
     * 타임어택 운동 제출 (민첩성)
     */
    @PostMapping("/submit/time-attack")
    public ResponseEntity<ChallengeSubmissionResponse> submitTimeAttackExercise(
            @Valid @RequestBody TimeAttackSubmissionRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("=== Time Attack Exercise Submission ===");
        log.info("Request: {}", request);
        
        Long userId = getCurrentUserIdFromJWT(httpRequest);
        log.info("User {} submitting time attack exercise {}", userId, request.getExerciseId());
        
        ChallengeSubmissionResponse response = timeAttackService.submitTimeAttackExercise(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 무게 기반 운동 제출 (근력)
     */
    @PostMapping("/submit/weight")
    public ResponseEntity<ChallengeSubmissionResponse> submitWeightExercise(
            @Valid @RequestBody WeightSubmissionRequest request,
            HttpServletRequest httpRequest) {
        
        Long userId = getCurrentUserIdFromJWT(httpRequest);
        log.info("User {} submitting weight exercise {}", userId, request.getExerciseId());
        
        ChallengeSubmissionResponse response = weightService.submitWeightExercise(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 지속시간 기반 운동 제출 (지속성)
     */
    @PostMapping("/submit/working-time")
    public ResponseEntity<ChallengeSubmissionResponse> submitWorkingTimeExercise(
            @Valid @RequestBody WorkingTimeSubmissionRequest request,
            HttpServletRequest httpRequest) {
        
        Long userId = getCurrentUserIdFromJWT(httpRequest);
        log.info("User {} submitting working time exercise {}", userId, request.getExerciseId());
        
        ChallengeSubmissionResponse response = workingTimeService.submitWorkingTimeExercise(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 챌린지 전체 제출 (배치 처리)
     */
    @PostMapping("/submit/batch")
    public ResponseEntity<ChallengeBatchSubmissionResponse> submitChallengeBatch(
            @Valid @RequestBody ChallengeBatchSubmissionRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("=== Challenge Batch Submission ===");
        log.info("Request: {}", request);
        
        Long userId = getCurrentUserIdFromJWT(httpRequest);
        log.info("User {} submitting challenge batch: {}", userId, request.getChallengeId());
        
        ChallengeBatchSubmissionResponse response = challengeBatchService.submitChallengeBatch(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 현재 주간 챌린지 조회 (타입별)
     */
    @GetMapping("/current")
    public ResponseEntity<WeeklyChallengeResponse> getCurrentChallenge(
            @RequestParam(defaultValue = "TIME_ATTACK") String challengeType) {
        
        ChallengeService service = getChallengeService(challengeType);
        WeeklyChallengeResponse response = service.getCurrentWeeklyChallenge();
        return ResponseEntity.ok(response);
    }
    
    /**
     * 특정 주간 챌린지 조회 (타입별)
     */
    @GetMapping("/weekly")
    public ResponseEntity<WeeklyChallengeResponse> getWeeklyChallenge(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "TIME_ATTACK") String challengeType) {
        
        ChallengeService service = getChallengeService(challengeType);
        WeeklyChallengeResponse response = service.getWeeklyChallenge(date);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 현재 사용자 포인트 조회 (타입별)
     */
    @GetMapping("/my-points")
    public ResponseEntity<Integer> getMyCurrentPoints(
            @RequestParam(defaultValue = "TIME_ATTACK") String challengeType,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        ChallengeService service = getChallengeService(challengeType);
        Integer points = service.getUserCurrentPoints(userId);
        return ResponseEntity.ok(points);
    }
    
    /**
     * 특정 주간 사용자 포인트 조회 (타입별)
     */
    @GetMapping("/my-points/weekly")
    public ResponseEntity<Integer> getMyWeeklyPoints(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "TIME_ATTACK") String challengeType,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        ChallengeService service = getChallengeService(challengeType);
        Integer points = service.getUserWeeklyPoints(userId, date);
        return ResponseEntity.ok(points);
    }
    
    /**
     * 현재 주간 리더보드 조회
     */
    @GetMapping("/leaderboard")
    public ResponseEntity<LeaderboardResponse> getCurrentLeaderboard(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        LeaderboardResponse response = leaderboardService.getCurrentWeeklyLeaderboard(userId, limit);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 특정 주간 리더보드 조회
     */
    @GetMapping("/leaderboard/weekly")
    public ResponseEntity<LeaderboardResponse> getWeeklyLeaderboard(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        LeaderboardResponse response = leaderboardService.getWeeklyLeaderboard(userId, date, limit);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 사용자 주변 순위 조회
     */
    @GetMapping("/leaderboard/surrounding")
    public ResponseEntity<LeaderboardResponse> getSurroundingRank(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        LocalDate targetDate = date != null ? date : LocalDate.now();
        
        LeaderboardResponse response = leaderboardService.getUserSurroundingRank(userId, targetDate);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 챌린지 타입에 따른 서비스 선택
     */
    private ChallengeService getChallengeService(String challengeType) {
        switch (challengeType.toUpperCase()) {
            case "TIME_ATTACK":
                return timeAttackService;
            case "WEIGHT":
                return weightService;
            case "WORKING_TIME":
                return workingTimeService;
            default:
                throw new IllegalArgumentException("지원하지 않는 챌린지 타입입니다: " + challengeType);
        }
    }
    
    /**
     * JWT에서 사용자 ID 추출
     */
    private Long getCurrentUserIdFromJWT(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header가 없거나 Bearer 토큰이 아닙니다.");
        }
        
        String jwt = authHeader.substring(7);
        
        try {
            // JWT에서 userId 추출
            Long userId = jwtUtils.extractUserId(jwt);
            log.info("Extracted userId from JWT: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("JWT에서 userId 추출 실패: {}", e.getMessage());
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
    }
    
    /**
     * 인증된 사용자 ID 추출 (Authentication 사용)
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("인증된 사용자가 아닙니다.");
        }
        
        // CustomUserPrincipal에서 사용자 ID 추출
        if (authentication.getPrincipal() instanceof com.healthmate.backendv2.auth.config.CustomUserPrincipal) {
            com.healthmate.backendv2.auth.config.CustomUserPrincipal userPrincipal = 
                (com.healthmate.backendv2.auth.config.CustomUserPrincipal) authentication.getPrincipal();
            
            Long userId = userPrincipal.getId();
            log.info("Extracted userId from authentication: {}", userId);
            return userId;
        }
        
        throw new IllegalArgumentException("유효하지 않은 인증 정보입니다.");
    }
}

package com.healthmate.backendv2.challenge.controller;

import com.healthmate.backendv2.challenge.dto.*;
import com.healthmate.backendv2.challenge.service.ChallengeBatchService;
import com.healthmate.backendv2.challenge.service.ChallengeService;
import com.healthmate.backendv2.challenge.service.ChallengeServiceWithTimeAttack;
import com.healthmate.backendv2.challenge.service.ChallengeServiceWithWeight;
import com.healthmate.backendv2.challenge.service.ChallengeServiceWithWorkingTime;
import com.healthmate.backendv2.challenge.service.ChallengeTemplateService;
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
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeUserController {

    private final LeaderboardService leaderboardService;
    private final ChallengeBatchService challengeBatchService;
    private final ChallengeTemplateService challengeTemplateService;
    private final JwtUtils jwtUtils;
    
    /**
     * 챌린지 전체 제출 (배치 처리)
     */
    @PostMapping("/submit")
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
     * 현재 활성화된 챌린지의 운동 목록 조회 (사용자용)
     */
    @GetMapping("/current/exercises")
    public ResponseEntity<List<Long>> getCurrentActiveExerciseIds() {
        List<Long> exerciseIds = challengeTemplateService.getCurrentActiveExerciseIds();
        return ResponseEntity.ok(exerciseIds);
    }

    /**
     * 현재 활성화된 챌린지 템플릿 조회 (사용자용)
     */
    @GetMapping("/current/template")
    public ResponseEntity<ChallengeTemplateResponse> getCurrentActiveTemplate() {
        return challengeTemplateService.getCurrentActiveTemplate()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

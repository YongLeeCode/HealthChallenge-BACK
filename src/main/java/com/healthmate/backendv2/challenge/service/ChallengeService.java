package com.healthmate.backendv2.challenge.service;

import com.healthmate.backendv2.challenge.dto.ChallengeSubmissionResponse;
import com.healthmate.backendv2.challenge.dto.WeeklyChallengeResponse;

import java.time.LocalDate;

/**
 * 챌린지 서비스의 공통 인터페이스
 * 각 챌린지 타입별로 구체적인 구현체가 필요
 */
public interface ChallengeService {
    
    /**
     * 현재 주간 챌린지 정보 조회
     */
    WeeklyChallengeResponse getCurrentWeeklyChallenge();
    
    /**
     * 특정 주간 챌린지 정보 조회
     */
    WeeklyChallengeResponse getWeeklyChallenge(LocalDate date);
    
    /**
     * 사용자의 현재 주간 포인트 조회
     */
    Integer getUserCurrentPoints(Long userId);
    
    /**
     * 사용자의 특정 주간 포인트 조회
     */
    Integer getUserWeeklyPoints(Long userId, LocalDate date);
    
    /**
     * 챌린지 타입 반환
     */
    String getChallengeType();
}

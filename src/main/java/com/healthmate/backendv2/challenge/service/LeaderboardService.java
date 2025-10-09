package com.healthmate.backendv2.challenge.service;

import com.healthmate.backendv2.challenge.dto.LeaderboardResponse;

import java.time.LocalDate;

public interface LeaderboardService {
    
    /**
     * 현재 주간 리더보드 조회
     */
    LeaderboardResponse getCurrentWeeklyLeaderboard(Long userId, int limit);
    
    /**
     * 특정 주간 리더보드 조회
     */
    LeaderboardResponse getWeeklyLeaderboard(Long userId, LocalDate date, int limit);
    
    /**
     * 사용자 주변 순위 조회 (상위 5명, 하위 5명)
     */
    LeaderboardResponse getUserSurroundingRank(Long userId, LocalDate date);
}

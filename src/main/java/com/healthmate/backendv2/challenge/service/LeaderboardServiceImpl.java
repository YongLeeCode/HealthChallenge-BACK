package com.healthmate.backendv2.challenge.service;

import com.healthmate.backendv2.challenge.dto.LeaderboardResponse;
import com.healthmate.backendv2.challenge.repository.ChallengeRedisRepository;
import com.healthmate.backendv2.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {
    
    private final ChallengeRedisRepository challengeRedisRepository;
    private final UserService userService;
    
    @Override
    public LeaderboardResponse getCurrentWeeklyLeaderboard(Long userId, int limit) {
        LocalDate currentWeek = getCurrentWeekStart();
        return getWeeklyLeaderboard(userId, currentWeek, limit);
    }
    
    @Override
    public LeaderboardResponse getWeeklyLeaderboard(Long userId, LocalDate date, int limit) {
        LocalDate weekStart = getWeekStart(date);
        String challengeKey = challengeRedisRepository.getWeeklyChallengeKey(weekStart);
        
        // 현재 사용자 정보 조회
        Double currentUserPoints = challengeRedisRepository.getUserPoints(challengeKey, userId);
        Long currentUserRank = challengeRedisRepository.getUserRank(challengeKey, userId);
        
        // 상위 N명 조회
        Set<ZSetOperations.TypedTuple<Object>> topRankers = challengeRedisRepository.getTopRankers(challengeKey, limit);
        
        // 전체 참가자 수
        Long totalParticipants = challengeRedisRepository.getTotalParticipants(challengeKey);
        
        // 리더보드 엔트리 생성
        List<LeaderboardResponse.LeaderboardEntry> entries = new ArrayList<>();
        int rank = 1;
        for (ZSetOperations.TypedTuple<Object> tuple : topRankers) {
            Long entryUserId = Long.valueOf(tuple.getValue().toString());
            Integer points = tuple.getScore().intValue();
            
            // 사용자 정보 조회
            var userResponse = userService.getById(entryUserId);
            if (userResponse != null) {
                entries.add(LeaderboardResponse.LeaderboardEntry.builder()
                        .rank(rank++)
                        .nickname(userResponse.getNickname())
                        .points(points)
                        .profileImageUrl(userResponse.getProfileImageUrl())
                        .build());
            }
        }
        
        String challengePeriod = weekStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + 
                               " ~ " + weekStart.plusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        return LeaderboardResponse.builder()
                .challengePeriod(challengePeriod)
                .currentUserRank(currentUserRank != null ? currentUserRank.intValue() : 0)
                .currentUserPoints(currentUserPoints != null ? currentUserPoints.intValue() : 0)
                .entries(entries)
                .totalParticipants(totalParticipants != null ? totalParticipants.intValue() : 0)
                .build();
    }
    
    @Override
    public LeaderboardResponse getUserSurroundingRank(Long userId, LocalDate date) {
        LocalDate weekStart = getWeekStart(date);
        String challengeKey = challengeRedisRepository.getWeeklyChallengeKey(weekStart);
        
        // 현재 사용자 순위 조회
        Long currentUserRank = challengeRedisRepository.getUserRank(challengeKey, userId);
        if (currentUserRank == null) {
            // 사용자가 아직 참여하지 않은 경우
            return LeaderboardResponse.builder()
                    .challengePeriod(weekStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + 
                                   " ~ " + weekStart.plusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .currentUserRank(0)
                    .currentUserPoints(0)
                    .entries(new ArrayList<>())
                    .totalParticipants(0)
                    .build();
        }
        
        // 상위 5명, 하위 5명 조회 (사용자 포함)
        long startRank = Math.max(0, currentUserRank - 5);
        long endRank = currentUserRank + 5;
        
        Set<ZSetOperations.TypedTuple<Object>> surroundingRankers = 
                challengeRedisRepository.getRankRange(challengeKey, startRank, endRank);
        
        // 리더보드 엔트리 생성
        List<LeaderboardResponse.LeaderboardEntry> entries = new ArrayList<>();
        int displayRank = (int) startRank + 1;
        for (ZSetOperations.TypedTuple<Object> tuple : surroundingRankers) {
            Long entryUserId = Long.valueOf(tuple.getValue().toString());
            Integer points = tuple.getScore().intValue();
            
            // 사용자 정보 조회
            var userResponse = userService.getById(entryUserId);
            if (userResponse != null) {
                entries.add(LeaderboardResponse.LeaderboardEntry.builder()
                        .rank(displayRank++)
                        .nickname(userResponse.getNickname())
                        .points(points)
                        .profileImageUrl(userResponse.getProfileImageUrl())
                        .build());
            }
        }
        
        // 현재 사용자 정보
        Double currentUserPoints = challengeRedisRepository.getUserPoints(challengeKey, userId);
        Long totalParticipants = challengeRedisRepository.getTotalParticipants(challengeKey);
        
        String challengePeriod = weekStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + 
                               " ~ " + weekStart.plusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        return LeaderboardResponse.builder()
                .challengePeriod(challengePeriod)
                .currentUserRank(currentUserRank.intValue())
                .currentUserPoints(currentUserPoints != null ? currentUserPoints.intValue() : 0)
                .entries(entries)
                .totalParticipants(totalParticipants != null ? totalParticipants.intValue() : 0)
                .build();
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
}

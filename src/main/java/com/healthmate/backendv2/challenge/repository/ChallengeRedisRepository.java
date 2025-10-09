package com.healthmate.backendv2.challenge.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Repository
public class ChallengeRedisRepository {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ZSetOperations<String, Object> zSetOperations;
    
    private static final String CHALLENGE_KEY_PREFIX = "challenge:weekly:";
    private static final String USER_POINTS_KEY_PREFIX = "challenge:user:points:";
    
    public ChallengeRedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.zSetOperations = redisTemplate.opsForZSet();
    }
    
    /**
     * 주간 챌린지 키 생성
     */
    public String getWeeklyChallengeKey(LocalDate date) {
        return CHALLENGE_KEY_PREFIX + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    
    /**
     * 사용자별 포인트 키 생성
     */
    public String getUserPointsKey(Long userId, LocalDate date) {
        return USER_POINTS_KEY_PREFIX + userId + ":" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    
    /**
     * 사용자 포인트 추가/업데이트
     */
    public void addUserPoints(String challengeKey, Long userId, double points) {
        zSetOperations.add(challengeKey, userId.toString(), points);
    }
    
    /**
     * 사용자 포인트 조회
     */
    public Double getUserPoints(String challengeKey, Long userId) {
        return zSetOperations.score(challengeKey, userId.toString());
    }
    
    /**
     * 사용자 순위 조회 (1부터 시작)
     */
    public Long getUserRank(String challengeKey, Long userId) {
        Long rank = zSetOperations.reverseRank(challengeKey, userId.toString());
        return rank != null ? rank + 1 : null;
    }
    
    /**
     * 상위 N명 리더보드 조회
     */
    public Set<ZSetOperations.TypedTuple<Object>> getTopRankers(String challengeKey, long limit) {
        return zSetOperations.reverseRangeWithScores(challengeKey, 0, limit - 1);
    }
    
    /**
     * 전체 참가자 수 조회
     */
    public Long getTotalParticipants(String challengeKey) {
        return zSetOperations.count(challengeKey, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }
    
    /**
     * 사용자 포인트 범위 조회 (특정 순위 범위)
     */
    public Set<ZSetOperations.TypedTuple<Object>> getRankRange(String challengeKey, long start, long end) {
        return zSetOperations.reverseRangeWithScores(challengeKey, start, end);
    }
    
    /**
     * 챌린지 데이터 삭제
     */
    public void deleteChallenge(String challengeKey) {
        redisTemplate.delete(challengeKey);
    }
    
    /**
     * 사용자별 포인트 히스토리 저장
     */
    public void saveUserPointsHistory(Long userId, LocalDate date, double points) {
        String key = getUserPointsKey(userId, date);
        redisTemplate.opsForValue().set(key, points);
    }
    
    /**
     * 사용자별 포인트 히스토리 조회
     */
    public Double getUserPointsHistory(Long userId, LocalDate date) {
        String key = getUserPointsKey(userId, date);
        Object points = redisTemplate.opsForValue().get(key);
        return points != null ? Double.valueOf(points.toString()) : 0.0;
    }
}

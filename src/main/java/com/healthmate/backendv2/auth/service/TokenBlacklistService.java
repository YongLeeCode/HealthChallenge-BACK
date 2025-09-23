package com.healthmate.backendv2.auth.service;

import com.healthmate.backendv2.auth.config.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtils jwtUtils;
    
    private static final String BLACKLIST_PREFIX = "blacklist:";
    
    /**
     * 토큰을 블랙리스트에 추가합니다.
     * 토큰의 만료 시간까지 TTL을 설정하여 자동으로 삭제되도록 합니다.
     */
    public void addToBlacklist(String token) {
        try {
            // 토큰의 만료 시간을 가져옵니다
            long expirationTime = jwtUtils.extractExpiration(token).getTime();
            long currentTime = System.currentTimeMillis();
            long ttlSeconds = (expirationTime - currentTime) / 1000;
            
            // TTL이 양수인 경우에만 블랙리스트에 추가
            if (ttlSeconds > 0) {
                String key = BLACKLIST_PREFIX + token;
                redisTemplate.opsForValue().set(key, "blacklisted", ttlSeconds, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            // 토큰 파싱에 실패한 경우 무시 (이미 만료되었거나 잘못된 토큰)
            // 로그를 남기고 계속 진행
        }
    }
    
    /**
     * 토큰이 블랙리스트에 있는지 확인합니다.
     */
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    /**
     * 토큰을 블랙리스트에서 제거합니다.
     */
    public void removeFromBlacklist(String token) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.delete(key);
    }
}

package com.healthmate.backendv2.auth.service;

import com.healthmate.backendv2.auth.config.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenBlacklistService 테스트")
class TokenBlacklistServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    private String testToken;
    private Date testExpiration;

    @BeforeEach
    void setUp() {
        testToken = "testAccessToken123";
        testExpiration = new Date(System.currentTimeMillis() + 900000); // 15분 후 만료

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("토큰을 블랙리스트에 추가 성공")
    void addToBlacklist_Success() {
        // Given
        when(jwtUtils.extractExpiration(testToken)).thenReturn(testExpiration);

        // When
        tokenBlacklistService.addToBlacklist(testToken);

        // Then
        verify(jwtUtils).extractExpiration(testToken);
        verify(valueOperations).set(
                eq("blacklist:" + testToken),
                eq("blacklisted"),
                anyLong(),
                eq(TimeUnit.SECONDS)
        );
    }

    @Test
    @DisplayName("블랙리스트 키 형식 확인")
    void verifyBlacklistKeyFormat() {
        // Given
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        when(jwtUtils.extractExpiration(token)).thenReturn(testExpiration);

        // When
        tokenBlacklistService.addToBlacklist(token);
        tokenBlacklistService.isBlacklisted(token);
        tokenBlacklistService.removeFromBlacklist(token);

        // Then
        verify(valueOperations).set(
                eq("blacklist:" + token),
                eq("blacklisted"),
                anyLong(),
                eq(TimeUnit.SECONDS)
        );
        verify(redisTemplate).hasKey("blacklist:" + token);
        verify(redisTemplate).delete("blacklist:" + token);
    }
}

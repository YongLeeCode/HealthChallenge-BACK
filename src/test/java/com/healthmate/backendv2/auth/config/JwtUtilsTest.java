package com.healthmate.backendv2.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtils 테스트")
class JwtUtilsTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtUtils jwtUtils;

    private static final String TEST_SECRET = "testSecretKeyForJwtTokenGenerationAndValidation123456789";
    private static final String TEST_ISSUER = "healthmate";
    private static final long ACCESS_TOKEN_EXPIRATION = 900000; // 15분
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000; // 7일

    @BeforeEach
    void setUp() {
        when(jwtProperties.getSecret()).thenReturn(TEST_SECRET);
        when(jwtProperties.getIssuer()).thenReturn(TEST_ISSUER);
        when(jwtProperties.getAccessTokenExpiration()).thenReturn(ACCESS_TOKEN_EXPIRATION);
        when(jwtProperties.getRefreshTokenExpiration()).thenReturn(REFRESH_TOKEN_EXPIRATION);
    }

    @Test
    @DisplayName("액세스 토큰 생성 성공")
    void generateAccessToken_Success() {
        // Given
        String nickname = "testuser";
        Long userId = 1L;

        // When
        String token = jwtUtils.generateAccessToken(nickname, userId);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT는 3개 부분으로 구성
    }

    @Test
    @DisplayName("리프레시 토큰 생성 성공")
    void generateRefreshToken_Success() {
        // Given
        String nickname = "testuser";
        Long userId = 1L;

        // When
        String token = jwtUtils.generateRefreshToken(nickname, userId);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT는 3개 부분으로 구성
    }

    @Test
    @DisplayName("토큰에서 닉네임 추출 성공")
    void extractNickname_Success() {
        // Given
        String nickname = "testuser";
        String token = jwtUtils.generateAccessToken(nickname, 1L);

        // When
        String extractedNickname = jwtUtils.extractNickname(token);

        // Then
        assertThat(extractedNickname).isEqualTo(nickname);
    }

    @Test
    @DisplayName("토큰에서 사용자 ID 추출 성공")
    void extractUserId_Success() {
        // Given
        Long userId = 123L;
        String token = jwtUtils.generateAccessToken("testuser", userId);

        // When
        Long extractedUserId = jwtUtils.extractUserId(token);

        // Then
        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    @DisplayName("토큰에서 토큰 타입 추출 성공")
    void extractTokenType_Success() {
        // Given
        String accessToken = jwtUtils.generateAccessToken("testuser", 1L);
        String refreshToken = jwtUtils.generateRefreshToken("testuser", 1L);

        // When
        String accessTokenType = jwtUtils.extractTokenType(accessToken);
        String refreshTokenType = jwtUtils.extractTokenType(refreshToken);

        // Then
        assertThat(accessTokenType).isEqualTo("access");
        assertThat(refreshTokenType).isEqualTo("refresh");
    }

    @Test
    @DisplayName("유효한 토큰 검증 성공")
    void validateToken_Success() {
        // Given
        String nickname = "testuser";
        String token = jwtUtils.generateAccessToken(nickname, 1L);

        // When
        Boolean isValid = jwtUtils.validateToken(token, nickname);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("잘못된 닉네임으로 토큰 검증 실패")
    void validateToken_Fail_WrongNickname() {
        // Given
        String token = jwtUtils.generateAccessToken("testuser", 1L);

        // When
        Boolean isValid = jwtUtils.validateToken(token, "wronguser");

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("잘못된 형식의 토큰 파싱 실패")
    void parseToken_Fail_InvalidFormat() {
        // Given
        String invalidToken = "invalid.token.format";

        // When & Then
        assertThatThrownBy(() -> jwtUtils.extractNickname(invalidToken))
                .isInstanceOf(MalformedJwtException.class);
    }

    @Test
    @DisplayName("토큰에 커스텀 클레임 포함 확인")
    void verifyCustomClaims() {
        // Given
        String nickname = "testuser";
        Long userId = 123L;
        String token = jwtUtils.generateAccessToken(nickname, userId);

        // When
        Claims claims = parseToken(token);

        // Then
        assertThat(claims.get("userId")).isEqualTo(userId);
        assertThat(claims.get("type")).isEqualTo("access");
        assertThat(claims.getSubject()).isEqualTo(nickname);
        assertThat(claims.getIssuer()).isEqualTo(TEST_ISSUER);
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
    }

    /**
     * 토큰을 파싱하여 Claims를 반환하는 헬퍼 메서드
     */
    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
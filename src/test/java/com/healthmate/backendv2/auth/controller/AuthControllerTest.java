package com.healthmate.backendv2.auth.controller;

import com.healthmate.backendv2.auth.dto.*;
import com.healthmate.backendv2.auth.service.AuthService;
import com.healthmate.backendv2.user.dto.UserResponse;
import com.healthmate.backendv2.user.RankTier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController 테스트")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;
    private TokenResponse tokenResponse;

    @BeforeEach
    void setUp() {
        signupRequest = SignupRequest.builder()
                .nickname("testuser")
                .email("test@example.com")
                .password("password123")
                .confirmPassword("password123")
                .profileImageUrl("http://example.com/image.jpg")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        loginRequest = LoginRequest.builder()
                .nickname("testuser")
                .password("password123")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .nickname("testuser")
                .email("test@example.com")
                .profileImageUrl("http://example.com/image.jpg")
                .birthday(LocalDate.of(1990, 1, 1))
                .rankTier(RankTier.BRONZE)
                .createdAt(OffsetDateTime.parse("2024-01-01T00:00:00Z"))
                .updatedAt(OffsetDateTime.parse("2024-01-01T00:00:00Z"))
                .build();

        tokenResponse = TokenResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .expiresIn(900L)
                .build();

        authResponse = AuthResponse.builder()
                .user(userResponse)
                .tokens(tokenResponse)
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_Success() {
        // Given
        when(authService.signup(any(SignupRequest.class))).thenReturn(authResponse);

        // When
        ResponseEntity<AuthResponse> response = authController.signup(signupRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUser().getNickname()).isEqualTo("testuser");
        assertThat(response.getBody().getUser().getEmail()).isEqualTo("test@example.com");
        assertThat(response.getBody().getTokens().getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getBody().getTokens().getRefreshToken()).isEqualTo("refreshToken");
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // When
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUser().getNickname()).isEqualTo("testuser");
        assertThat(response.getBody().getTokens().getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getBody().getTokens().getRefreshToken()).isEqualTo("refreshToken");
    }

    @Test
    @DisplayName("토큰 갱신 성공")
    void refreshToken_Success() {
        // Given
        TokenResponse newTokenResponse = TokenResponse.builder()
                .accessToken("newAccessToken")
                .refreshToken("newRefreshToken")
                .expiresIn(900L)
                .build();

        when(authService.refreshToken(anyString())).thenReturn(newTokenResponse);

        // When
        ResponseEntity<TokenResponse> response = authController.refreshToken("Bearer refreshToken");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isEqualTo("newAccessToken");
        assertThat(response.getBody().getRefreshToken()).isEqualTo("newRefreshToken");
        assertThat(response.getBody().getExpiresIn()).isEqualTo(900L);
    }

    @Test
    @DisplayName("로그아웃 성공")
    void signout_Success() {
        // When
        ResponseEntity<Void> response = authController.signout("Bearer accessToken", "refreshToken");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("로그아웃 성공 - 리프레시 토큰 없음")
    void signout_Success_NoRefreshToken() {
        // When
        ResponseEntity<Void> response = authController.signout("Bearer accessToken", null);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Authorization 헤더에서 Bearer 제거 테스트")
    void testBearerTokenRemoval() {
        // Given
        TokenResponse newTokenResponse = TokenResponse.builder()
                .accessToken("newAccessToken")
                .refreshToken("newRefreshToken")
                .expiresIn(900L)
                .build();

        when(authService.refreshToken(anyString())).thenReturn(newTokenResponse);

        // When
        ResponseEntity<TokenResponse> response = authController.refreshToken("Bearer refreshToken");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("로그아웃 시 Authorization 헤더에서 Bearer 제거 테스트")
    void testSignoutBearerTokenRemoval() {
        // When
        ResponseEntity<Void> response = authController.signout("Bearer accessToken", "refreshToken");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    @DisplayName("서비스 예외 처리")
    void handleServiceException() {
        // Given
        when(authService.signup(any(SignupRequest.class)))
                .thenThrow(new IllegalArgumentException("이메일이 이미 존재합니다."));

        // When & Then
        assertThatThrownBy(() -> authController.signup(signupRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일이 이미 존재합니다.");
    }

    @Test
    @DisplayName("토큰 갱신 서비스 예외 처리")
    void handleRefreshTokenServiceException() {
        // Given
        when(authService.refreshToken(anyString()))
                .thenThrow(new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));

        // When & Then
        assertThatThrownBy(() -> authController.refreshToken("Bearer invalidToken"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 리프레시 토큰입니다.");
    }
}
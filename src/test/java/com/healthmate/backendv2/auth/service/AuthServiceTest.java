package com.healthmate.backendv2.auth.service;

import com.healthmate.backendv2.auth.config.JwtUtils;
import com.healthmate.backendv2.auth.dto.*;
import com.healthmate.backendv2.auth.entity.RefreshToken;
import com.healthmate.backendv2.auth.repository.RefreshTokenRepository;
import com.healthmate.backendv2.user.dto.UserResponse;
import com.healthmate.backendv2.user.entity.User;
import com.healthmate.backendv2.user.repository.UserRepository;
import com.healthmate.backendv2.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 테스트")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private SignupRequest signupRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .nickname("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .profileImageUrl("http://example.com/image.jpg")
                .birthday(LocalDate.of(1990, 1, 1))
                .rankTier(com.healthmate.backendv2.user.RankTier.BRONZE)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

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
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_Success() {
        // Given
        when(userRepository.findByNickname("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtils.generateAccessToken("testuser", 1L)).thenReturn("accessToken");
        when(jwtUtils.generateRefreshToken("testuser", 1L)).thenReturn("refreshToken");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken());

        // When
        AuthResponse response = authService.signup(signupRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getTokens()).isNotNull();
        assertThat(response.getUser().getNickname()).isEqualTo("testuser");
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
        assertThat(response.getTokens().getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getTokens().getRefreshToken()).isEqualTo("refreshToken");

        verify(userRepository).findByNickname("testuser");
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtUtils).generateAccessToken("testuser", 1L);
        verify(jwtUtils).generateRefreshToken("testuser", 1L);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 불일치")
    void signup_Fail_PasswordMismatch() {
        // Given
        SignupRequest invalidRequest = SignupRequest.builder()
                .nickname("testuser")
                .email("test@example.com")
                .password("password123")
                .confirmPassword("differentPassword")
                .profileImageUrl("http://example.com/image.jpg")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        // When & Then
        assertThatThrownBy(() -> authService.signup(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호와 비밀번호 확인이 일치하지 않습니다.");

        verify(userRepository, never()).findByNickname(anyString());
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복")
    void signup_Fail_DuplicateNickname() {
        // Given
        when(userRepository.findByNickname("testuser")).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> authService.signup(signupRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("닉네임이 이미 존재합니다.");

        verify(userRepository).findByNickname("testuser");
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_Fail_DuplicateEmail() {
        // Given
        when(userRepository.findByNickname("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> authService.signup(signupRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일이 이미 존재합니다.");

        verify(userRepository).findByNickname("testuser");
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // Given
        when(userRepository.findByNickname("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtils.generateAccessToken("testuser", 1L)).thenReturn("accessToken");
        when(jwtUtils.generateRefreshToken("testuser", 1L)).thenReturn("refreshToken");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken());

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getTokens()).isNotNull();
        assertThat(response.getUser().getNickname()).isEqualTo("testuser");
        assertThat(response.getTokens().getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getTokens().getRefreshToken()).isEqualTo("refreshToken");

        verify(userRepository).findByNickname("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtUtils).generateAccessToken("testuser", 1L);
        verify(jwtUtils).generateRefreshToken("testuser", 1L);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_Fail_UserNotFound() {
        // Given
        when(userRepository.findByNickname("testuser")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("닉네임 또는 비밀번호가 올바르지 않습니다.");

        verify(userRepository).findByNickname("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtils, never()).generateAccessToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_Fail_WrongPassword() {
        // Given
        when(userRepository.findByNickname("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("닉네임 또는 비밀번호가 올바르지 않습니다.");

        verify(userRepository).findByNickname("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtUtils, never()).generateAccessToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("토큰 갱신 성공")
    void refreshToken_Success() {
        // Given
        String refreshTokenValue = "validRefreshToken";
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .userId(1L)
                .expiresAt(OffsetDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        when(jwtUtils.validateToken(refreshTokenValue)).thenReturn(true);
        when(jwtUtils.extractTokenType(refreshTokenValue)).thenReturn("refresh");
        when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(jwtUtils.generateAccessToken("testuser", 1L)).thenReturn("newAccessToken");
        when(jwtUtils.generateRefreshToken("testuser", 1L)).thenReturn("newRefreshToken");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken());

        // When
        TokenResponse response = authService.refreshToken("Bearer " + refreshTokenValue);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
        assertThat(response.getRefreshToken()).isEqualTo("newRefreshToken");

        verify(jwtUtils).validateToken(refreshTokenValue);
        verify(jwtUtils).extractTokenType(refreshTokenValue);
        verify(refreshTokenRepository).findByToken(refreshTokenValue);
        verify(userRepository).findById(1L);
        verify(jwtUtils).generateAccessToken("testuser", 1L);
        verify(jwtUtils).generateRefreshToken("testuser", 1L);
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 유효하지 않은 토큰")
    void refreshToken_Fail_InvalidToken() {
        // Given
        String invalidToken = "invalidToken";
        when(jwtUtils.validateToken(invalidToken)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken("Bearer " + invalidToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 리프레시 토큰입니다.");

        verify(jwtUtils).validateToken(invalidToken);
        verify(jwtUtils, never()).extractTokenType(anyString());
        verify(refreshTokenRepository, never()).findByToken(anyString());
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 잘못된 토큰 타입")
    void refreshToken_Fail_WrongTokenType() {
        // Given
        String accessToken = "accessToken";
        when(jwtUtils.validateToken(accessToken)).thenReturn(true);
        when(jwtUtils.extractTokenType(accessToken)).thenReturn("access");

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken("Bearer " + accessToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 토큰 타입입니다.");

        verify(jwtUtils).validateToken(accessToken);
        verify(jwtUtils).extractTokenType(accessToken);
        verify(refreshTokenRepository, never()).findByToken(anyString());
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 데이터베이스에 없는 토큰")
    void refreshToken_Fail_TokenNotFoundInDB() {
        // Given
        String refreshTokenValue = "validRefreshToken";
        when(jwtUtils.validateToken(refreshTokenValue)).thenReturn(true);
        when(jwtUtils.extractTokenType(refreshTokenValue)).thenReturn("refresh");
        when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken("Bearer " + refreshTokenValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 리프레시 토큰입니다.");

        verify(jwtUtils).validateToken(refreshTokenValue);
        verify(jwtUtils).extractTokenType(refreshTokenValue);
        verify(refreshTokenRepository).findByToken(refreshTokenValue);
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 만료된 토큰")
    void refreshToken_Fail_ExpiredToken() {
        // Given
        String refreshTokenValue = "expiredRefreshToken";
        RefreshToken expiredToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .userId(1L)
                .expiresAt(OffsetDateTime.now().minusDays(1)) // 만료된 토큰
                .revoked(false)
                .build();

        when(jwtUtils.validateToken(refreshTokenValue)).thenReturn(true);
        when(jwtUtils.extractTokenType(refreshTokenValue)).thenReturn("refresh");
        when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.of(expiredToken));

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken("Bearer " + refreshTokenValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("만료되거나 무효화된 리프레시 토큰입니다.");

        verify(jwtUtils).validateToken(refreshTokenValue);
        verify(jwtUtils).extractTokenType(refreshTokenValue);
        verify(refreshTokenRepository).findByToken(refreshTokenValue);
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 무효화된 토큰")
    void refreshToken_Fail_RevokedToken() {
        // Given
        String refreshTokenValue = "revokedRefreshToken";
        RefreshToken revokedToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .userId(1L)
                .expiresAt(OffsetDateTime.now().plusDays(7))
                .revoked(true) // 무효화된 토큰
                .build();

        when(jwtUtils.validateToken(refreshTokenValue)).thenReturn(true);
        when(jwtUtils.extractTokenType(refreshTokenValue)).thenReturn("refresh");
        when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.of(revokedToken));

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken("Bearer " + refreshTokenValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("만료되거나 무효화된 리프레시 토큰입니다.");

        verify(jwtUtils).validateToken(refreshTokenValue);
        verify(jwtUtils).extractTokenType(refreshTokenValue);
        verify(refreshTokenRepository).findByToken(refreshTokenValue);
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("로그아웃 성공")
    void signout_Success() {
        // Given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .userId(1L)
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(refreshTokenEntity));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshTokenEntity);

        // When
        authService.signout(accessToken, refreshToken);

        // Then
        verify(tokenBlacklistService).addToBlacklist(accessToken);
        verify(refreshTokenRepository).findByToken(refreshToken);
        verify(refreshTokenRepository).save(refreshTokenEntity);
        assertThat(refreshTokenEntity.isRevoked()).isTrue();
    }

    @Test
    @DisplayName("로그아웃 성공 - 리프레시 토큰 없음")
    void signout_Success_NoRefreshToken() {
        // Given
        String accessToken = "accessToken";

        // When
        authService.signout(accessToken, null);

        // Then
        verify(tokenBlacklistService).addToBlacklist(accessToken);
        verify(refreshTokenRepository, never()).findByToken(anyString());
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("로그아웃 성공 - 존재하지 않는 리프레시 토큰")
    void signout_Success_RefreshTokenNotFound() {
        // Given
        String accessToken = "accessToken";
        String refreshToken = "nonexistentRefreshToken";

        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.empty());

        // When
        authService.signout(accessToken, refreshToken);

        // Then
        verify(tokenBlacklistService).addToBlacklist(accessToken);
        verify(refreshTokenRepository).findByToken(refreshToken);
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }
}

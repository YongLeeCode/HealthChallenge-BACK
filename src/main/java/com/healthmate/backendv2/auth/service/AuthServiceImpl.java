package com.healthmate.backendv2.auth.service;

import com.healthmate.backendv2.auth.config.JwtUtils;
import com.healthmate.backendv2.auth.dto.*;
import com.healthmate.backendv2.auth.entity.RefreshToken;
import com.healthmate.backendv2.auth.repository.RefreshTokenRepository;
import com.healthmate.backendv2.user.dto.UserResponse;
import com.healthmate.backendv2.user.entity.User;
import com.healthmate.backendv2.user.repository.UserRepository;
import com.healthmate.backendv2.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistService tokenBlacklistService;
    
    @Override
    public AuthResponse signup(SignupRequest request) {
        // 비밀번호 확인 검증
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
        
        // 닉네임 중복 확인
        if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new IllegalArgumentException("닉네임이 이미 존재합니다.");
        }
        
        // 이메일 중복 확인
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이메일이 이미 존재합니다.");
        }
        
        // 사용자 생성
        User user = User.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profileImageUrl(request.getProfileImageUrl())
                .birthday(request.getBirthday())
                .rankTier(com.healthmate.backendv2.user.RankTier.BRONZE)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        
        User savedUser = userRepository.save(user);
        
        // 토큰 생성
        TokenResponse tokens = generateTokens(savedUser.getNickname(), savedUser.getId());
        
        return AuthResponse.builder()
                .user(UserResponse.from(savedUser))
                .tokens(tokens)
                .build();
    }
    
    @Override
    public AuthResponse login(LoginRequest request) {
        // 사용자 조회
        User user = userRepository.findByNickname(request.getNickname())
                .orElseThrow(() -> new IllegalArgumentException("닉네임 또는 비밀번호가 올바르지 않습니다."));
        
        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("닉네임 또는 비밀번호가 올바르지 않습니다.");
        }
        
        // 토큰 생성
        TokenResponse tokens = generateTokens(user.getNickname(), user.getId());
        
        return AuthResponse.builder()
                .user(UserResponse.from(user))
                .tokens(tokens)
                .build();
    }
    
    @Override
    public TokenResponse refreshToken(String token) {
		String refreshTokenValue = token.replace("Bearer ", "")
			.trim()
			.replaceAll("\\s+", "")
			.replaceAll("^\"|\"$", "");

        // 리프레시 토큰 검증
        if (!jwtUtils.validateToken(refreshTokenValue)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }
        
        String tokenType = jwtUtils.extractTokenType(refreshTokenValue);
        if (!"refresh".equals(tokenType)) {
            throw new IllegalArgumentException("잘못된 토큰 타입입니다.");
        }
        
        // 데이터베이스에서 리프레시 토큰 확인
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));
        
        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("만료되거나 무효화된 리프레시 토큰입니다.");
        }
        
        // 사용자 조회
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 새 토큰 생성
        return generateTokens(user.getNickname(), user.getId());
    }
    
    @Override
    public void signout(String accessToken, String refreshToken) {
        // 액세스 토큰을 블랙리스트에 추가
        tokenBlacklistService.addToBlacklist(accessToken);
        
        // 리프레시 토큰이 있으면 무효화
        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(rt -> {
                        rt.setRevoked(true);
                        refreshTokenRepository.save(rt);
                    });
        }
    }
    
    private TokenResponse generateTokens(String username, Long userId) {
        String accessToken = jwtUtils.generateAccessToken(username, userId);
        String refreshToken = jwtUtils.generateRefreshToken(username, userId);
        
        // 리프레시 토큰을 데이터베이스에 저장
        OffsetDateTime expiresAt = OffsetDateTime.now().plusDays(7); // 7일
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .userId(userId)
                .expiresAt(expiresAt)
                .createdAt(OffsetDateTime.now())
                .revoked(false)
                .build();
        
        refreshTokenRepository.save(refreshTokenEntity);
        
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(900L) // 15분
                .build();
    }
    
}

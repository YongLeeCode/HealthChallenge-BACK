package com.healthmate.healthmate.domain.auth.service;

import com.healthmate.healthmate.domain.auth.dto.AuthDtos.SignInRequest;
import com.healthmate.healthmate.domain.auth.dto.AuthDtos.SignUpRequest;
import com.healthmate.healthmate.domain.auth.dto.AuthDtos.TokenResponse;
import com.healthmate.healthmate.domain.user.entity.User;
import com.healthmate.healthmate.domain.user.entity.UserRole;
import com.healthmate.healthmate.domain.user.repository.UserRepository;
import com.healthmate.healthmate.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void signUp(SignUpRequest req) {
		if (userRepository.existsByEmail(req.email())) {
			throw new IllegalArgumentException("Email already in use");
		}
		User user = new User(req.email(), passwordEncoder.encode(req.password()), UserRole.USER);
		userRepository.save(user);
	}

	@Override
	public TokenResponse signIn(SignInRequest req) {
		User user = userRepository.findByEmail(req.email())
			.orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
		if (!passwordEncoder.matches(req.password(), user.getPassword())) {
			throw new IllegalArgumentException("Invalid credentials");
		}
		String token = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole());
		return new TokenResponse(token);
	}
}



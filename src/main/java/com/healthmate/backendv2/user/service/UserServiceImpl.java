package com.healthmate.backendv2.user.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;

import com.healthmate.backendv2.user.dto.UserDtos.*;
import com.healthmate.backendv2.user.entity.User;
import com.healthmate.backendv2.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public Response getById(Long id) {
		return Response.from(userRepository.findById(id).orElseThrow());
	}

	@Override
	public Response updateProfile(Long id, ProfileUpdateRequest request) {
		User user = userRepository.findById(id).orElseThrow();
		
		user = User.builder()
				.id(user.getId())
				.username(request.getUsername())
				.email(request.getEmail())
				.password(user.getPassword()) // 기존 비밀번호 유지
				.profileImageUrl(request.getProfileImageUrl())
				.birthday(request.getBirthday())
				.rankTier(user.getRankTier()) // 기존 랭크 유지
				.createdAt(user.getCreatedAt()) // 기존 생성일 유지
				.updatedAt(user.getUpdatedAt()) // JPA가 자동으로 업데이트
				.build();
		
		User savedUser = userRepository.save(user);
		return Response.from(savedUser);
	}

	@Override
	public void changePassword(Long id, PasswordChangeRequest request) {
		User user = userRepository.findById(id).orElseThrow();
		
		// 현재 비밀번호 확인
		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
			throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
		}
		
		// 새 비밀번호로 업데이트
		user = User.builder()
				.id(user.getId())
				.username(user.getUsername())
				.email(user.getEmail())
				.password(passwordEncoder.encode(request.getNewPassword()))
				.profileImageUrl(user.getProfileImageUrl())
				.birthday(user.getBirthday())
				.rankTier(user.getRankTier())
				.createdAt(user.getCreatedAt())
				.updatedAt(user.getUpdatedAt())
				.build();
		
		userRepository.save(user);
	}
}



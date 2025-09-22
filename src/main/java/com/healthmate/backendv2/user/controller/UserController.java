package com.healthmate.backendv2.user.controller;

import lombok.RequiredArgsConstructor;

import com.healthmate.backendv2.user.dto.PasswordChangeRequest;
import com.healthmate.backendv2.user.dto.ProfileUpdateRequest;
import com.healthmate.backendv2.user.dto.UserResponse;
import com.healthmate.backendv2.user.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getById(id));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<UserResponse> updateProfile(@PathVariable Long id,
		@RequestBody ProfileUpdateRequest request) {
		return ResponseEntity.ok(userService.updateProfile(id, request));
	}

	@PatchMapping("/{id}/password")
	public ResponseEntity<String> changePassword(@PathVariable Long id, @RequestBody PasswordChangeRequest request) {
		userService.changePassword(id, request);
		return ResponseEntity.ok("비밀번호 변경 성공");
	}
}



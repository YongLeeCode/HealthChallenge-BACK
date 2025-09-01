package com.healthmate.healthmate.domain.auth.controller;

import com.healthmate.healthmate.domain.auth.dto.AuthDtos.SignInRequest;
import com.healthmate.healthmate.domain.auth.dto.AuthDtos.SignUpRequest;
import com.healthmate.healthmate.domain.auth.dto.AuthDtos.TokenResponse;
import com.healthmate.healthmate.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<Void> signUp(@RequestBody SignUpRequest req) {
		authService.signUp(req);
		return ResponseEntity.status(201).build();
	}

	@PostMapping("/signin")
	public ResponseEntity<TokenResponse> signIn(@RequestBody SignInRequest req) {
		return ResponseEntity.ok(authService.signIn(req));
	}
}



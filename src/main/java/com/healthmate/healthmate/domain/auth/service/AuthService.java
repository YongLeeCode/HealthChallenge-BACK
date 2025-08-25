package com.healthmate.healthmate.domain.auth.service;

import com.healthmate.healthmate.domain.auth.dto.AuthDtos.SignInRequest;
import com.healthmate.healthmate.domain.auth.dto.AuthDtos.SignUpRequest;
import com.healthmate.healthmate.domain.auth.dto.AuthDtos.TokenResponse;

public interface AuthService {
	void signUp(SignUpRequest req);
	TokenResponse signIn(SignInRequest req);
}



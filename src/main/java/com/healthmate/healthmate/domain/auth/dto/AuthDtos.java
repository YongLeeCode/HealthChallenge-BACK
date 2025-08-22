package com.healthmate.healthmate.domain.auth.dto;

public class AuthDtos {
	public record SignUpRequest(String email, String password) {}
	public record SignInRequest(String email, String password) {}
	public record TokenResponse(String accessToken) {}
}



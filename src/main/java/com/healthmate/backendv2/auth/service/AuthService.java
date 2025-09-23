package com.healthmate.backendv2.auth.service;

import com.healthmate.backendv2.auth.dto.*;

public interface AuthService {
    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
    TokenResponse refreshToken(String refreshToken);
    void signout(String accessToken, String refreshToken);
}

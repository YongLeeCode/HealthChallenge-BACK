package com.healthmate.backendv2.auth.dto;

import com.healthmate.backendv2.user.dto.UserResponse;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthResponse {
    UserResponse user;
    TokenResponse tokens;
}

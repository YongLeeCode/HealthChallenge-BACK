package com.healthmate.backendv2.auth.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TokenResponse {
    String accessToken;
    String refreshToken;
    String tokenType = "Bearer";
    Long expiresIn; // seconds
}

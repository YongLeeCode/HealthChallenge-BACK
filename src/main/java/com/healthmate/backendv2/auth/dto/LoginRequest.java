package com.healthmate.backendv2.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginRequest {
    @NotBlank String nickname;
    @NotBlank String password;
}

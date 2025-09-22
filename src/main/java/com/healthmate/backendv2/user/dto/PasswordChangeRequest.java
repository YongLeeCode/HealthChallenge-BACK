package com.healthmate.backendv2.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PasswordChangeRequest {
    @NotBlank String currentPassword;
    @NotBlank String newPassword;
}

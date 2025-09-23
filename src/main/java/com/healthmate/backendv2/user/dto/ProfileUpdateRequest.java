package com.healthmate.backendv2.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class ProfileUpdateRequest {
    @NotBlank String nickname;
    @Email @NotBlank String email;
    String profileImageUrl;
    @Past LocalDate birthday;
}

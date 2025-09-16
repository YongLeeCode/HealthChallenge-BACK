package com.healthmate.backendv2.user.dto;

import com.healthmate.backendv2.user.RankTier;
import com.healthmate.backendv2.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class UserDtos {

    @Value
    @Builder
    public static class CreateRequest {
        @NotBlank String username;
        @NotBlank String password;
        @NotBlank @Email String email;
        String profileImageUrl;
        @Past LocalDate birthday;
        RankTier rankTier;
    }

    @Value
    @Builder
    public static class Response {
        @NotNull Long id;
        String username;
        String email;
        String profileImageUrl;
        LocalDate birthday;
        RankTier rankTier;
        OffsetDateTime createdAt;
        OffsetDateTime updatedAt;

        public static Response from(User user) {
            return Response.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .profileImageUrl(user.getProfileImageUrl())
                    .birthday(user.getBirthday())
                    .rankTier(user.getRankTier())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
        }
    }
}



package com.healthmate.backendv2.user.dto;

import com.healthmate.backendv2.user.RankTier;
import com.healthmate.backendv2.user.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Value
@Builder
public class UserResponse {
    @NotNull Long id;
    String nickname;
    String email;
    String profileImageUrl;
    LocalDate birthday;
    RankTier rankTier;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .birthday(user.getBirthday())
                .rankTier(user.getRankTier())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

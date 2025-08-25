package com.healthmate.healthmate.domain.user.dto;

import com.healthmate.healthmate.domain.user.entity.UserRole;

public record UpdateUserRoleRequestDto(Long userId, UserRole role) {}



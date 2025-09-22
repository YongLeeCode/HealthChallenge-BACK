package com.healthmate.backendv2.user.service;

import com.healthmate.backendv2.user.dto.UserResponse;
import com.healthmate.backendv2.user.dto.ProfileUpdateRequest;
import com.healthmate.backendv2.user.dto.PasswordChangeRequest;

public interface UserService {
    UserResponse getById(Long id);
    UserResponse updateProfile(Long id, ProfileUpdateRequest request);
    void changePassword(Long id, PasswordChangeRequest request);
}

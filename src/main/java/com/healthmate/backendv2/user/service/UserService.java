package com.healthmate.backendv2.user.service;

import com.healthmate.backendv2.user.dto.UserDtos.*;

public interface UserService {
    Response getById(Long id);
    Response updateProfile(Long id, ProfileUpdateRequest request);
    void changePassword(Long id, PasswordChangeRequest request);
}

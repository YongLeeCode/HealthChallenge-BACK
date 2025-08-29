package com.healthmate.healthmate.domain.user.service;

import com.healthmate.healthmate.domain.user.dto.UpdateUserRoleRequestDto;
import com.healthmate.healthmate.domain.user.entity.User;
import com.healthmate.healthmate.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updateUserRole(UpdateUserRoleRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.email())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + requestDto.email()));
        user.setRole(requestDto.role());
    }
}



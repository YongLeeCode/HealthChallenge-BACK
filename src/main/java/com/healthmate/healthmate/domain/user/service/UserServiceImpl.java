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
        User user = userRepository.findById(requestDto.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + requestDto.userId()));
        user.setRole(requestDto.role());
        // JPA dirty checking will persist the change at transaction commit
    }
}



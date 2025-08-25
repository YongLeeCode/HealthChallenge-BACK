package com.healthmate.healthmate.domain.user.controller;

import com.healthmate.healthmate.domain.user.dto.UpdateUserRoleRequestDto;
import com.healthmate.healthmate.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateUserRole(@RequestBody UpdateUserRoleRequestDto requestDto) {
        userService.updateUserRole(requestDto);
        return ResponseEntity.ok().build();
    }
}



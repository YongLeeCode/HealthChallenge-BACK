package com.healthmate.backendv2.user.controller;

import lombok.RequiredArgsConstructor;

import com.healthmate.backendv2.user.dto.UserDtos.Response;
import com.healthmate.backendv2.user.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }
}



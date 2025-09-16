package com.healthmate.backendv2.user.controller;

import com.healthmate.backendv2.user.dto.UserDtos.Response;
import com.healthmate.backendv2.user.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getById(@PathVariable Long id) {
        return userService.getById(id)
                .map(user -> ResponseEntity.ok(Response.from(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}



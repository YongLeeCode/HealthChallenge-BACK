package com.healthmate.backendv2.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.healthmate.backendv2.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
}



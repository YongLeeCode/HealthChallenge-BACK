package com.healthmate.healthmate.global.security;

import com.healthmate.healthmate.domain.user.entity.User;
import com.healthmate.healthmate.domain.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public final class SecurityUtils {
	
	private final UserRepository userRepository;
	
	public SecurityUtils(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public static Long getCurrentUserIdOrThrow() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getPrincipal() == null) {
			throw new IllegalStateException("Unauthenticated");
		}
		try {
			return Long.parseLong(String.valueOf(auth.getPrincipal()));
		} catch (NumberFormatException e) {
			throw new IllegalStateException("Invalid principal");
		}
	}
	
	public User getCurrentUser() {
		Long userId = getCurrentUserIdOrThrow();
		return userRepository.findById(userId)
				.orElseThrow(() -> new IllegalStateException("User not found"));
	}
}



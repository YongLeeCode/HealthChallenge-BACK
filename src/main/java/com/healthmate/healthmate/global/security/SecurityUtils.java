package com.healthmate.healthmate.global.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
	private SecurityUtils() {}

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
}



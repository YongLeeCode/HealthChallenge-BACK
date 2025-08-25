package com.healthmate.healthmate.global.security;

import com.healthmate.healthmate.domain.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

	private final Key key;
	private final long accessTokenValidityMs;

	public JwtTokenProvider(
			@Value("${security.jwt.secret}") String secret,
			@Value("${security.jwt.access-validity-ms:3600000}") long accessTokenValidityMs
	) {
		this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
		this.accessTokenValidityMs = accessTokenValidityMs;
	}

	public String createAccessToken(Long userId, String email, UserRole role) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + accessTokenValidityMs);
		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.claim("email", email)
			.claim("role", role.name())
			.setIssuedAt(now)
			.setExpiration(expiry)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public Claims parseClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}
}



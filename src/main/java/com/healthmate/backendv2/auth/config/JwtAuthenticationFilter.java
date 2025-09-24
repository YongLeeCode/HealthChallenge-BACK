package com.healthmate.backendv2.auth.config;

import com.healthmate.backendv2.auth.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String nickname;
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        jwt = authHeader.substring(7);
        
        try {
            // refresh token인 경우 인증 처리하지 않음
            String tokenType = jwtUtils.extractTokenType(jwt);
            if ("refresh".equals(tokenType)) {
                filterChain.doFilter(request, response);
                return;
            }
            
            nickname = jwtUtils.extractNickname(jwt);
            
            if (nickname != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 토큰이 블랙리스트에 있는지 확인
                if (tokenBlacklistService.isBlacklisted(jwt)) {
                    filterChain.doFilter(request, response);
                    return;
                }
                
                // 토큰 유효성 검증
                if (jwtUtils.validateToken(jwt)) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(nickname);
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // 토큰이 유효하지 않은 경우 로그를 남기고 계속 진행
            logger.error("JWT 토큰 처리 중 오류 발생", e);
        }
        
        filterChain.doFilter(request, response);
    }
}

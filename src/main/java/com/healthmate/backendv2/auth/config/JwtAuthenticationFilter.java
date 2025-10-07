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
                logger.warn("Refresh token used for authentication: {}");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\":\"Refresh token cannot be used for authentication\"}");
                return;
            }
            
            nickname = jwtUtils.extractNickname(jwt);
            
            if (nickname != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 토큰이 블랙리스트에 있는지 확인
                if (tokenBlacklistService.isBlacklisted(jwt)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\":\"Token is blacklisted\"}");
                    return;
                }
                
                // 토큰 유효성 검증
                if (jwtUtils.validateToken(jwt)) {
                    // JWT에서 userId 추출
                    Long userId = jwtUtils.extractUserId(jwt);
                    logger.info("Extracted userId from JWT: {}" + userId);
                    
                    UserDetails userDetails = userDetailsService.loadUserByUsername(nickname);
                    
                    // CustomUserPrincipal로 변환하여 userId 포함
                    if (userDetails instanceof CustomUserPrincipal) {
                        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) userDetails;
                        // userId가 다르면 업데이트
                        if (!userId.equals(customUserPrincipal.getId())) {
                            customUserPrincipal = CustomUserPrincipal.builder()
                                    .id(userId)
                                    .nickname(customUserPrincipal.getNickname())
                                    .password(customUserPrincipal.getPassword())
                                    .authorities(customUserPrincipal.getAuthorities())
                                    .build();
                        }
                        
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                customUserPrincipal, null, customUserPrincipal.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        // 기본 UserDetails인 경우 CustomUserPrincipal로 변환
                        CustomUserPrincipal customUserPrincipal = CustomUserPrincipal.builder()
                                .id(userId)
                                .nickname(userDetails.getUsername())
                                .password(userDetails.getPassword())
                                .authorities(userDetails.getAuthorities())
                                .build();
                        
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                customUserPrincipal, null, customUserPrincipal.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\":\"Invalid token\"}");
                    return;
                }
            } else if (nickname == null) {
                logger.warn("Could not extract nickname from token");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\":\"Invalid token format\"}");
                return;
            }
        } catch (Exception e) {
            // 토큰이 유효하지 않은 경우 로그를 남기고 에러 응답
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\":\"Token processing error: " + e.getMessage() + "\"}");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}

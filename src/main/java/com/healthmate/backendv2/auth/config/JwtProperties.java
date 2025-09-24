package com.healthmate.backendv2.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret = "mySecretKey123456789012345678901234567890";
    private long accessTokenExpiration = 900000; // 15분 (밀리초)
    private long refreshTokenExpiration = 604800000; // 7일 (밀리초)
    private String issuer = "healthmate";
}

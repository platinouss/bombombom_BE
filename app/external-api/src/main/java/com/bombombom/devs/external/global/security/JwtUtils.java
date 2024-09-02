package com.bombombom.devs.external.global.security;

import com.bombombom.devs.core.util.Clock;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtUtils {

    @Value("${spring.security.jwt.secret-key}")
    private String secretKey;

    @Value("${spring.security.jwt.access-token.expire-length}")
    private long accessTokenExpiration;

    @Value("${spring.security.jwt.refresh-token.expire-length}")
    private long refreshTokenExpiration;

    private final Clock clock;

    public String generateAccessToken(Authentication authentication) {
        return Jwts.builder()
            .setSubject(authentication.getName())
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .setExpiration(clock.calculateFutureDateFromNow(accessTokenExpiration))
            .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        return Jwts.builder()
            .setSubject(authentication.getName())
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .setExpiration(clock.calculateFutureDateFromNow(refreshTokenExpiration))
            .compact();
    }

    public String extractUserId(String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}

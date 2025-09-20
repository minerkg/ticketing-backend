package org.ubb.ticketing.service.user;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.ubb.ticketing.domain.user.UserRole;
import org.ubb.ticketing.util.KeyUtil;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;


    private static final long ACCESS_TOKEN_VALIDITY = 60 * 60;       // 1 hour
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60; // 7 days

    public JwtService(KeyUtil keyUtil) throws Exception {
        this.privateKey = keyUtil.loadPrivateKey("private.pem");
        this.publicKey = keyUtil.loadPublicKey("public.pem");

    }


    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(refreshToken);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }


    public String generateAccessToken(String username, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .claim("authorities", List.of(role.replace("ROLE_", "")))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ACCESS_TOKEN_VALIDITY)))
                .signWith(privateKey, io.jsonwebtoken.SignatureAlgorithm.RS256)
                .compact();
    }


    public String generateRefreshToken(Authentication authentication) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(REFRESH_TOKEN_VALIDITY)))
                .signWith(privateKey, io.jsonwebtoken.SignatureAlgorithm.RS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

}



package com.sahil.notification.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtTokenService {

    private static final String SECRET =
            System.getenv("REALESTATE_JWT_SECRET");

    private final SecretKey signingKey;

    public JwtTokenService() {
        if (SECRET == null || SECRET.isBlank()) {
            throw new IllegalStateException(
                    "REALESTATE_JWT_SECRET environment variable is missing"
            );
        }

        this.signingKey =
                Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        Claims claims = extractClaims(token);

        return claims.getSubject() != null
                && !claims.getSubject().isBlank()
                && claims.getExpiration() != null
                && claims.getExpiration().after(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

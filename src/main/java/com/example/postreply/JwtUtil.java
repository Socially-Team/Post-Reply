package com.example.postreply;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.util.Date;

@Component
public class JwtUtil {

    // 保持与生成token的微服务一致的 SECRET
    //private final String SECRET = "mySecretKey123456789123456789123456789123456789";

    @Value("${jwt.secret}")
    private String SECRET;
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired", e);
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Invalid token format", e);
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid token signature", e);
        } catch (Exception e) {
            throw new RuntimeException("Token validation error", e);
        }
    }

    public String extractEmail(String token) {
        return validateToken(token).getSubject();
    }

    public Long extractUserId(String token) {
        return validateToken(token).get("userId", Long.class);
    }

    public String extractRole(String token) {
        return validateToken(token).get("role", String.class);
    }
}
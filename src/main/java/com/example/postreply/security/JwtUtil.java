package com.example.postreply.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class JwtUtil {

    private final String SECRET_KEY;

    public JwtUtil(@Value("${SECRET_KEY:mySecretKey}") String secretKey) {
        SECRET_KEY = secretKey;
    }

    /*
    for simplicity, not using custom exception handler, simply throw the exception catch outside
    and set response to unauthorized with message "invalid jwt token"
    */
    public void validateToken(String token) throws Exception {
        try{
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
        }catch(Exception e){
            throw new Exception("Invalid token");
        }
    }

    public  Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

}


/*
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
*/
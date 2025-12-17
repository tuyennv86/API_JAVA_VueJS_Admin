package com.example.api_backend.security;

import com.example.api_backend.model.Permission;
import com.example.api_backend.model.Role;
import com.example.api_backend.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.accessTokenExpirationMs}")
    private long jwtExpirationMs;

    private SecretKey getKey() {
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        } catch (Exception ex) {
            return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        }
    }
    public String extractTokenFromRequest(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        String jti = UUID.randomUUID().toString();

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream().map(Role::getName).toList());
        claims.put("perms", user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(Permission::getName)
                .distinct().toList());

        return Jwts.builder()
                .id(jti)
                .subject(user.getUsername())
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(jwtExpirationMs)))
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public String getJtiFromToken(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload().getId();
    }

    public Date getExpirationDate(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload().getExpiration();
    }
}

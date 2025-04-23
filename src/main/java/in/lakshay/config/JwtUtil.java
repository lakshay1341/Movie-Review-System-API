package in.lakshay.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import in.lakshay.entity.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Base64;

@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        // Initialize the key once and reuse it
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        String roleName = user.getRole().getName();
        // Ensure role has ROLE_ prefix
        String roleWithPrefix = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
        claims.put("roles", List.of(roleWithPrefix));

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUserName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        log.debug("Extracting claims from token: {}", token.substring(0, Math.min(10, token.length())) + "...");
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            log.debug("Claims extracted successfully: {}", claims);
            return claims;
        } catch (Exception e) {
            log.error("Error extracting claims from token: {}", e.getMessage());
            throw e;
        }
    }

    public String getUsernameFromToken(String token) {
        log.debug("Getting username from token");
        String username = getClaimsFromToken(token).getSubject();
        log.debug("Username from token: {}", username);
        return username;
    }

    public Long getUserIdFromToken(String token) {
        return getClaimsFromToken(token).get("userId", Long.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        log.debug("Getting roles from token");
        List<String> roles = getClaimsFromToken(token).get("roles", List.class);
        log.debug("Roles from token: {}", roles);
        return roles;
    }

    public boolean isTokenExpired(String token) {
        log.debug("Checking if token is expired");
        Date expiration = getClaimsFromToken(token).getExpiration();
        boolean isExpired = expiration.before(new Date());
        log.debug("Token expired: {}, expiration: {}", isExpired, expiration);
        return isExpired;
    }
}
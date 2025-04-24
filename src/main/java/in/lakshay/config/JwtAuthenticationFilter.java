package in.lakshay.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.debug("Request URI: {}", requestURI);

        // Skip authentication for webhook endpoint
        if (requestURI.equals("/api/v1/payments/webhook")) {
            log.debug("Skipping authentication for webhook endpoint");
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        log.debug("Auth Header: {}", authHeader);

        if (authHeader == null) {
            log.debug("No Authorization header found, continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            log.debug("Authorization header does not start with 'Bearer ', continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Bearer token found: {}", authHeader);

        try {
            String token = authHeader.substring(7);
            if (!jwtUtil.isTokenExpired(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                List<String> roles = jwtUtil.getRolesFromToken(token);

                log.debug("Username from token: {}", username);
                log.debug("Roles from token: {}", roles);

                List<GrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                    .collect(Collectors.toList());

                log.debug("Authorities created: {}", authorities);

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authentication set in SecurityContext: {}", authentication);
                log.debug("Current authentication: {}", SecurityContextHolder.getContext().getAuthentication());
                log.debug("Current authentication authorities: {}", SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            }
        } catch (Exception e) {
            log.error("Authentication error: ", e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
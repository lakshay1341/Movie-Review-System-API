package in.lakshay.config;

import in.lakshay.entity.User;
import in.lakshay.entity.UserPrincipal;
import in.lakshay.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Development-only authentication provider that provides a fallback mechanism
 * for authentication when the standard authentication fails.
 *
 * This is only active in the "h2" profile and should never be used in production.
 */
@Component
@Profile("h2")
@Slf4j
public class DevAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    // Using BCryptPasswordEncoder directly to avoid circular dependency
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        log.debug("DevAuthenticationProvider attempting to authenticate user: {}", username);

        // Try to find the user
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        // Check if the password is "password" for development testing
        if ("password".equals(password)) {
            log.warn("DEV MODE: Allowing login with test password for user: {}", username);
            UserPrincipal principal = new UserPrincipal(user);
            return new UsernamePasswordAuthenticationToken(
                    principal,
                    password,
                    principal.getAuthorities());
        }

        // Otherwise, try normal password check
        if (passwordEncoder.matches(password, user.getPassword().replace("{bcrypt}", ""))) {
            log.debug("DevAuthenticationProvider: Password matched for user: {}", username);
            UserPrincipal principal = new UserPrincipal(user);
            return new UsernamePasswordAuthenticationToken(
                    principal,
                    password,
                    principal.getAuthorities());
        }

        throw new BadCredentialsException("Invalid username or password");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

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
 * DEV ONLY auth provider - gives us a backdoor for testing
 * when the normal auth is being difficult
 *
 * NEVER EVER use this in production!! Only for h2 profile.
 */
@Component
@Profile("h2") // only active in h2 profile
@Slf4j
public class DevAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository; // to look up users

    // using encoder directly to avoid circular dependency
    // spring security config is a mess sometimes lol
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        log.debug("DevAuthProvider trying to auth user: {}", username);

        // find the user or bail
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        // BACKDOOR: magic password for dev testing
        // this is the magic - just use "password" to login as anyone in dev mode
        if ("password".equals(password)) {
            log.warn("DEV MODE: Letting {} login with backdoor password!", username);
            UserPrincipal principal = new UserPrincipal(user);
            return new UsernamePasswordAuthenticationToken(
                    principal,
                    password,
                    principal.getAuthorities());
        }

        // fallback to normal password check if backdoor not used
        if (passwordEncoder.matches(password, user.getPassword().replace("{bcrypt}", ""))) {
            log.debug("DevAuthProvider: Password matched for {}", username);
            UserPrincipal principal = new UserPrincipal(user);
            return new UsernamePasswordAuthenticationToken(
                    principal,
                    password,
                    principal.getAuthorities());
        }

        // nope, bad password
        throw new BadCredentialsException("Invalid username or password");
    }

    // tell spring what kind of auth we support
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    // TODO: remove this before going to prod!!!
}

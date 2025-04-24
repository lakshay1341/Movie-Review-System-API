package in.lakshay.config;

import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfig {
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public AuthenticationProvider authProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/v1/auth/**").permitAll()
				.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**").permitAll()
				.requestMatchers("/error").permitAll()
				.requestMatchers("/api/v1/movies/**").permitAll()
				.requestMatchers("/api/v1/theaters/**").permitAll()
				.requestMatchers("/api/v1/showtimes/**").permitAll()
				.requestMatchers("/api/v1/seats/showtimes/**").permitAll()
				.requestMatchers("/api/v1/payments/webhook").permitAll()
				.anyRequest().authenticated()
			)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authenticationProvider(authProvider())
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling(ex -> ex
				.authenticationEntryPoint((request, response, authException) -> {
					log.error("Unauthorized error: {}", authException.getMessage());
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
				})
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					log.error("Access denied error: {}", accessDeniedException.getMessage());
					response.sendError(HttpServletResponse.SC_FORBIDDEN, "Error: Forbidden");
				})
			);

		return http.build();
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}

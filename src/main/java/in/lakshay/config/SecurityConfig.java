package in.lakshay.config;

import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfig {
	@Value("${spring.web.cors.allowed-origins:http://localhost:5173}")
	private String allowedOrigins;

	@Value("${spring.web.cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
	private String allowedMethods;

	@Value("${spring.web.cors.allowed-headers:Authorization,Content-Type,X-Requested-With,Accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers}")
	private String allowedHeaders;

	@Value("${spring.web.cors.exposed-headers:Authorization,Content-Type}")
	private String exposedHeaders;

	@Value("${spring.web.cors.allow-credentials:true}")
	private boolean allowCredentials;

	@Value("${spring.web.cors.max-age:3600}")
	private long maxAge;
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired(required = false)
	private DevAuthenticationProvider devAuthenticationProvider;

	@Bean
	public AuthenticationProvider authProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		Map<String, PasswordEncoder> encoders = new HashMap<>();
		encoders.put("bcrypt", new BCryptPasswordEncoder(12));
		return new DelegatingPasswordEncoder("bcrypt", encoders);
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		// Register our authentication providers
		AuthenticationManager manager = authConfig.getAuthenticationManager();

		// Add development authentication provider if available
		if (devAuthenticationProvider != null) {
			log.warn("DEV MODE: Adding development authentication provider as fallback");
		}

		return manager;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/v1/auth/**").permitAll()
				.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**").permitAll()
				.requestMatchers("/error").permitAll()
				.requestMatchers("/api/v1/movies/**").permitAll()
				.requestMatchers("/api/v1/theaters/**").permitAll()
				.requestMatchers("/api/v1/showtimes/**").permitAll()
				.requestMatchers("/api/v1/seats/showtimes/**").permitAll()
				.requestMatchers("/api/v1/payments/webhook").permitAll()
				.requestMatchers("/api/v1/health").permitAll()
				.requestMatchers("/api/v1/diagnostics/**").authenticated()
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

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// Split the allowed origins and convert to a list
		String[] origins = allowedOrigins.split(",");
		for (String origin : origins) {
			configuration.addAllowedOriginPattern(origin.trim());
		}

		// Set allowed methods
		configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));

		// Set allowed headers
		configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));

		// Set exposed headers
		configuration.setExposedHeaders(Arrays.asList(exposedHeaders.split(",")));

		// Set credentials and max age
		configuration.setAllowCredentials(allowCredentials);
		configuration.setMaxAge(maxAge);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}

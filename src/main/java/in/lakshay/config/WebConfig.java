package in.lakshay.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// web mvc config - mainly for CORS stuff
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // CORS config from properties with defaults

    @Value("${spring.web.cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins; // frontend origins that can access our API

    @Value("${spring.web.cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
    private String allowedMethods; // http methods allowed

    @Value("${spring.web.cors.allowed-headers:Authorization,Content-Type,X-Requested-With,Accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers}")
    private String allowedHeaders; // headers browser can send

    @Value("${spring.web.cors.exposed-headers:Authorization,Content-Type}")
    private String exposedHeaders; // headers browser can access

    @Value("${spring.web.cors.allow-credentials:true}")
    private boolean allowCredentials; // allow cookies etc

    @Value("${spring.web.cors.max-age:3600}")
    private long maxAge; // browser caching of preflight in seconds

    // configure CORS for all endpoints
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // apply to all paths
                .allowedOriginPatterns(allowedOrigins.split(",")) // using patterns for docker/render
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders(allowedHeaders.split(","))
                .exposedHeaders(exposedHeaders.split(","))
                .allowCredentials(allowCredentials) // needed for auth
                .maxAge(maxAge); // cache preflight for 1hr
    }

    // TODO: maybe add resource handlers for static content later
}

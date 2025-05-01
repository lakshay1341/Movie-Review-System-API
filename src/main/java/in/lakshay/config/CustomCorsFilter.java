package in.lakshay.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter to add CORS headers to all responses.
 * This ensures CORS headers are applied even for error responses.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomCorsFilter implements Filter {

    @Value("${spring.web.cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    @Value("${spring.web.cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
    private String allowedMethods;

    @Value("${spring.web.cors.allowed-headers:Authorization,Content-Type,X-Requested-With,Accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers}")
    private String allowedHeaders;

    @Value("${spring.web.cors.exposed-headers:Authorization,Content-Type}")
    private String exposedHeaders;

    @Value("${spring.web.cors.allow-credentials:true}")
    private String allowCredentials;

    @Value("${spring.web.cors.max-age:3600}")
    private String maxAge;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        // Get the first origin from the list if there are multiple
        String origin = allowedOrigins.split(",")[0].trim();

        // For containerized environments, we use pattern matching in SecurityConfig
        // but here we need to handle the specific origin for the preflight request
        if ("*".equals(origin) || origin.contains("://frontend")) {
            // In production Docker environment, set the specific origin from the request
            String requestOrigin = request.getHeader("Origin");
            if (requestOrigin != null) {
                response.setHeader("Access-Control-Allow-Origin", requestOrigin);
            } else {
                response.setHeader("Access-Control-Allow-Origin", origin);
            }
        } else {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        response.setHeader("Access-Control-Allow-Methods", allowedMethods.replace(",", ", "));
        response.setHeader("Access-Control-Allow-Headers", allowedHeaders.replace(",", ", "));
        response.setHeader("Access-Control-Expose-Headers", exposedHeaders.replace(",", ", "));
        response.setHeader("Access-Control-Allow-Credentials", allowCredentials);
        response.setHeader("Access-Control-Max-Age", maxAge);

        // For preflight requests (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // No initialization needed
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}

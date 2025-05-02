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
 * Special CORS filter that adds headers to ALL responses including errors.
 * Spring's default CORS handling doesn't work for error responses, so we need this.
 *
 * This runs before everything else in the filter chain.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // make sure this runs first
public class CustomCorsFilter implements Filter {

    // same CORS settings as in WebConfig
    // kinda duplicated but we need them in both places :/

    @Value("${spring.web.cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins; // frontend urls

    @Value("${spring.web.cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
    private String allowedMethods; // http methods

    @Value("${spring.web.cors.allowed-headers:Authorization,Content-Type,X-Requested-With,Accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers}")
    private String allowedHeaders; // headers client can send

    @Value("${spring.web.cors.exposed-headers:Authorization,Content-Type}")
    private String exposedHeaders; // headers client can read

    @Value("${spring.web.cors.allow-credentials:true}")
    private String allowCredentials; // allow cookies

    @Value("${spring.web.cors.max-age:3600}")
    private String maxAge; // preflight cache time

    // this runs for every request
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        // just use first origin if multiple are configured
        String origin = allowedOrigins.split(",")[0].trim();

        // special handling for docker/render deployment
        // this is tricky - we need to reflect the actual origin in containerized envs
        if ("*".equals(origin) || origin.contains("://frontend")) {
            // get the actual origin from the request header
            String requestOrigin = request.getHeader("Origin");
            if (requestOrigin != null) {
                // echo back the actual origin that made the request
                response.setHeader("Access-Control-Allow-Origin", requestOrigin);
            } else {
                // fallback to configured origin
                response.setHeader("Access-Control-Allow-Origin", origin);
            }
        } else {
            // use configured origin for local dev
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        // add all the CORS headers
        // add spaces after commas for readability
        response.setHeader("Access-Control-Allow-Methods", allowedMethods.replace(",", ", "));
        response.setHeader("Access-Control-Allow-Headers", allowedHeaders.replace(",", ", "));
        response.setHeader("Access-Control-Expose-Headers", exposedHeaders.replace(",", ", "));
        response.setHeader("Access-Control-Allow-Credentials", allowCredentials);
        response.setHeader("Access-Control-Max-Age", maxAge);

        // handle preflight OPTIONS requests specially
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            // just return 200 OK with the CORS headers, no body needed
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            // normal request - continue the filter chain
            chain.doFilter(req, res);
        }
    }

    // required filter methods - we don't need to do anything in these

    @Override
    public void init(FilterConfig filterConfig) {
        // nothing to init
    }

    @Override
    public void destroy() {
        // nothing to clean up
    }

    // TODO: maybe add some debug logging?
}

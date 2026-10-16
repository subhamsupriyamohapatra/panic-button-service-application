package com.emergency.panicbutton.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Mirrors FastAPI's {@code HTTPBearer} security: every {@code /api/v1/**}
 * request except the auth endpoints must carry a valid JWT. The resolved user
 * id is exposed as a request attribute for the controllers.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String AUTH_USER_ATTRIBUTE = "authUserId";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/api/v1/auth",
            "/health",
            "/ready",
            "/v3/api-docs",
            "/api/v1/openapi.json",
            "/swagger-ui",
            "/swagger-ui.html",
            "/docs",
            "/error"
    );

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (isPublic(path) || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Only the API surface is protected.
        if (!path.startsWith("/api/v1")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            writeUnauthorized(response);
            return;
        }

        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        try {
            UUID userId = jwtService.extractUserId(token);
            request.setAttribute(AUTH_USER_ATTRIBUTE, userId);
        } catch (Exception e) {
            writeUnauthorized(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublic(String path) {
        return PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
    }

    private void writeUnauthorized(HttpServletResponse response)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"success\":false,"
                        + "\"message\":\"Missing or invalid authentication token\","
                        + "\"errorCode\":\"UNAUTHORIZED\","
                        + "\"details\":null}");
    }
}

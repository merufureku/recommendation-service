package com.merufureku.aromatica.recommendation_service.config;

import com.merufureku.aromatica.recommendation_service.utilities.TokenUtility;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger logger = LogManager.getLogger(RateLimitFilter.class);

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final TokenUtility tokenUtility;

    private static final Map<String, RateLimitConfig> ENDPOINT_LIMITS = Map.of(
            "/recommendations/cf", new RateLimitConfig(30, Duration.ofMinutes(1), RateLimitScope.USER),
            "/recommendations/cbf", new RateLimitConfig(30, Duration.ofMinutes(1), RateLimitScope.USER)

    );

    public RateLimitFilter(TokenUtility tokenUtility) {
        this.tokenUtility = tokenUtility;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI()
                .replace(request.getContextPath(), "");

        RateLimitConfig config = findMatchingConfig(path);

        if (config != null) {
            String key = resolveKey(request, path, config.scope);
            Bucket bucket = resolveBucket(key, config);

            if (bucket.tryConsume(1)) {
                logger.debug("Rate limit check passed: key={}, path={}, remaining={}", key, path, bucket.getAvailableTokens());
                filterChain.doFilter(request, response);
            } else {
                logger.warn("Rate limit exceeded: key={}, path={}", key, path);
                sendRateLimitError(response, request.getRequestURI());
            }
        } else {
            logger.debug("No rate limit configured for path: {}", path);
            filterChain.doFilter(request, response);
        }
    }

    private RateLimitConfig findMatchingConfig(String path) {
        // Iterate through patterns in order (more specific first due to LinkedHashMap)
        for (Map.Entry<String, RateLimitConfig> entry : ENDPOINT_LIMITS.entrySet()) {
            String pattern = entry.getKey();
            if (matchesPattern(path, pattern)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Matches a path against a pattern, supporting path variables like {id}
     * Examples:
     *   - "/public/fragrances/123" matches "/public/fragrances/{id}"
     *   - "/public/fragrances" matches "/public/fragrances"
     */
    private boolean matchesPattern(String path, String pattern) {
        // Convert pattern to regex: {id} becomes [^/]+
        String regexPattern = pattern
                .replaceAll("\\{[^}]+}", "[^/]+")
                .replaceAll("/", "\\\\/");
        return Pattern.matches("^" + regexPattern + "$", path);
    }

    private String resolveKey(HttpServletRequest request, String path, RateLimitScope scope) {
        return switch (scope) {
            case IP -> "ip:" + getClientIP(request) + ":" + path;
            case USER -> {
                String userId = extractUserId(request);
                yield userId != null ? "user:" + userId + ":" + path
                        : "ip:" + getClientIP(request) + ":" + path; // Fallback to IP
            }
            case GLOBAL -> "global:" + path; // All users share same limit
        };
    }

    private String extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                var claims = tokenUtility.parseToken(token);
                Integer userId = claims.get("userId", Integer.class);
                return userId != null ? userId.toString() : null;
            } catch (Exception e) {
                logger.debug("Failed to extract userId from token: {}", e.getMessage());
                return null;
            }
        }
        return null;
    }

    private Bucket resolveBucket(String key, RateLimitConfig config) {
        return cache.computeIfAbsent(key, k -> createBucket(config));
    }

    private Bucket createBucket(RateLimitConfig config) {
        Bandwidth limit = Bandwidth.classic(
                config.capacity,
                Refill.intervally(config.capacity, config.duration)
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }

        String xrfHeader = request.getHeader("X-Real-IP");
        if (xrfHeader != null && !xrfHeader.isEmpty()) {
            return xrfHeader;
        }

        return request.getRemoteAddr();
    }

    private void sendRateLimitError(HttpServletResponse response, String path) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write(
                String.format(
                        "{\"status\":429,\"error\":\"Too Many Requests\"," +
                                "\"message\":\"Rate limit exceeded. Please try again later.\"," +
                                "\"path\":\"%s\"}",
                        path
                )
        );
    }

    /**
     * Rate limiting scope determines what is being limited
     */
    private enum RateLimitScope {
        IP,      // Limit per IP address (for unauthenticated endpoints)
        USER,    // Limit per user ID (for authenticated endpoints)
        GLOBAL   // Limit total requests to endpoint (all users combined)
    }

    private record RateLimitConfig(long capacity, Duration duration, RateLimitScope scope) {}
}
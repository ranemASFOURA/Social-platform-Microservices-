package com.example.Gateway_Service.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import java.nio.charset.StandardCharsets;

@Component
@Order(0)

public class JwtAuthenticationFilter implements GlobalFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Value("${jwt.secret}")
    private String secret;
    private static final List<String> openApiEndpoints = List.of(
            "/api/auth/login",
            "/api/users/signup",
            "/api/images/generate-upload-url",
            "/api/image-proxy",
            "/images",
            "/fallback/images",
            "/cdn/");

    private boolean isSecured(String path) {
        return openApiEndpoints.stream().noneMatch(path::contains);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String path = request.getURI().getPath();
        logger.info("[Gateway] Forwarding request to route: {}", path);
        logger.info(" Gateway RECEIVED: {} {}", request.getMethod(), path);
        logger.info("[Gateway] Query Params: {}", request.getQueryParams());
        logger.info("[Gateway] Headers: {}", request.getHeaders());

        if (!isSecured(path)) {
            logger.info("[Gateway] Public endpoint detected: {} - Skipping JWT validation", path);
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(secret)
                        .parseClaimsJws(token)
                        .getBody();

                String userId = claims.getSubject();
                String role = claims.get("type", String.class);

                logger.info("[Gateway] JWT valid. userId={}, role={}", userId, role);

                ServerHttpRequest modifiedRequest = request.mutate()
                        .headers(httpHeaders -> {
                            httpHeaders.setAll(request.getHeaders().toSingleValueMap());

                            httpHeaders.add("X-User-Id", userId);
                            httpHeaders.add("X-User-Role", role);
                        })
                        .build();

                logger.info("[Gateway] Forwarding request to downstream with headers X-User-Id={}, X-User-Role={}",
                        userId, role);
                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                logger.error("[Gateway] JWT parsing failed: {}", e.getMessage());
                return onError(exchange, "Your session has expired. Please log in again.", HttpStatus.UNAUTHORIZED);
            }

        } else {
            logger.warn("[Gateway] Missing or malformed Authorization header");
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        String json = String.format("{\"error\": \"%s\"}", message);
        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(json.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

}

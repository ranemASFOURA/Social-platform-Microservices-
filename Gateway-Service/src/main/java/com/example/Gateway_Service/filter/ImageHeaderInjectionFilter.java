package com.example.Gateway_Service.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class ImageHeaderInjectionFilter implements GatewayFilterFactory<ImageHeaderInjectionFilter.Config> {

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 1. /api/image-proxy/user-images/xxx.webp
            String fullPath = exchange.getRequest().getURI().getPath();

            // 2. /user-images/xxx.webp
            String imagePath = fullPath.replaceFirst("/api/image-proxy", "");

            // 3. header
            ServerHttpRequest modifiedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-Original-Image-Path", imagePath)
                    .build();

            // 4.
            ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
            return chain.filter(modifiedExchange);
        };
    }

    @Override
    public Config newConfig() {
        return new Config();
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

    @Override
    public String name() {
        return "InjectImageHeader"; // هذا الاسم سنستخدمه في application.yml
    }
}

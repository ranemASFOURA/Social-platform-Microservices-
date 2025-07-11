package com.example.Gateway_Service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

@RestController
@RequestMapping("/fallback/images")
public class ImageFallbackController {

    @Value("${minio.base-url}")
    private String minioBaseUrl;

    private final WebClient webClient = WebClient.create();
    private static final MediaType IMAGE_WEBP = MediaType.parseMediaType("image/webp");

    @GetMapping
    public Mono<ResponseEntity<ByteArrayResource>> fallback(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getHeaders().getFirst("X-Original-Image-Path");

        if (path == null || path.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        String fullUrl = minioBaseUrl + path;
        System.out.println("[Fallback] Fetching from MinIO: " + fullUrl);

        return webClient.get()
                .uri(fullUrl)
                .exchangeToMono(clientResponse -> {
                    MediaType contentType = clientResponse.headers()
                            .contentType()
                            .orElse(MediaType.APPLICATION_OCTET_STREAM);

                    return clientResponse.bodyToMono(byte[].class)
                            .map(bytes -> {
                                ByteArrayResource resource = new ByteArrayResource(bytes);
                                HttpHeaders headers = new HttpHeaders();
                                headers.setContentType(contentType);
                                headers.setContentLength(bytes.length); // optional but good
                                return new ResponseEntity<>(resource, headers, HttpStatus.OK);
                            });
                })
                .onErrorResume(e -> {
                    System.err.println("[Fallback] Failed: " + e.getMessage());
                    return Mono.just(ResponseEntity.notFound().build());
                });
    }

}
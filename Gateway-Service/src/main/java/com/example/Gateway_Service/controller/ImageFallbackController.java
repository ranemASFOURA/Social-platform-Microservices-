package com.example.Gateway_Service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/fallback/images")
public class ImageFallbackController {

    @Value("${minio.base-url}")
    private String minioBaseUrl;
    @Autowired
    private WebClient webClient;

    private static final MediaType IMAGE_WEBP = MediaType.parseMediaType("image/webp");
    private static final Logger logger = LoggerFactory.getLogger(ImageFallbackController.class);

    @GetMapping
    public Mono<ResponseEntity<ByteArrayResource>> fallback(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getHeaders().getFirst("X-Original-Image-Path");
        logger.info("[Gateway] Request path: " + path);
        System.out.println("[Fallback] Incoming fallback request to /fallback/images");
        System.out.println("[Fallback] X-Original-Image-Path = " + path);

        if (path == null || path.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        String fullUrl = minioBaseUrl + path.replace("/cdn", "");
        logger.info("[Fallback] Fetching from MinIO: {}", fullUrl);

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
                                headers.setContentLength(bytes.length);
                                return new ResponseEntity<>(resource, headers, HttpStatus.OK);
                            });
                })
                .onErrorResume(e -> {
                    logger.warn("[Fallback] Failed to fetch image:: {}", e.getMessage());
                    return Mono.just(ResponseEntity.notFound().build());
                });
    }

}
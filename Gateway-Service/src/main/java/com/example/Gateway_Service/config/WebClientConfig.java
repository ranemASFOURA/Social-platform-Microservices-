package com.example.Gateway_Service.config;

import io.micrometer.tracing.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.*;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(Tracer tracer) {
        return WebClient.builder()
                .filter((request, next) -> {
                    var span = tracer.currentSpan();
                    if (span != null) {
                        ClientRequest newRequest = ClientRequest.from(request)
                                .header("X-B3-TraceId", span.context().traceId())
                                .header("X-B3-SpanId", span.context().spanId())
                                .build();
                        return next.exchange(newRequest);
                    }
                    return next.exchange(request);
                })
                .build();
    }
}

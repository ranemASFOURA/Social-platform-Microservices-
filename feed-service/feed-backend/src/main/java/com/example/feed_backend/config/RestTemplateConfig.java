package com.example.feed_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import io.micrometer.tracing.Tracer;


@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, Tracer tracer) {
        return builder
            .additionalInterceptors((request, body, execution) -> {
                var span = tracer.currentSpan();
                if (span != null) {
                    request.getHeaders().add("X-B3-TraceId", span.context().traceId());
                    request.getHeaders().add("X-B3-SpanId", span.context().spanId());
                }
                return execution.execute(request, body);
            })
            .build();
    }
}

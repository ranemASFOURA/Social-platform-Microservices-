package com.example.post_backend.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import io.micrometer.tracing.Tracer;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.Message;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Tracer tracer;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate, Tracer tracer) {
        this.kafkaTemplate = kafkaTemplate;
        this.tracer = tracer;
    }

    public void sendPostCreatedEvent(String postId, String userId, String imageUrl, String caption, String createdAt) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventType", "post.created");
        payload.put("postId", postId);
        payload.put("userId", userId);
        payload.put("imageUrl", imageUrl);
        payload.put("caption", caption);
        payload.put("timestamp", createdAt);
        Message<Map<String, Object>> message = MessageBuilder.withPayload(payload)
                .setHeader("X-B3-TraceId", tracer.currentSpan().context().traceId())
                .setHeader("X-B3-SpanId", tracer.currentSpan().context().spanId())
                .build();
        System.out.println("Sending Kafka post.created event...");

        kafkaTemplate.send("post.created", message);
        System.out.println("Event sent successfully.");
    }
}

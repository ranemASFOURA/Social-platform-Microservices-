package com.example.user_backend.kafka;

import com.example.user_backend.dto.*;
import com.example.user_backend.model.User;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import io.micrometer.tracing.Tracer;
import org.springframework.messaging.Message;

@Component
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Tracer tracer;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, Tracer tracer) {
        this.kafkaTemplate = kafkaTemplate;
        this.tracer = tracer;
    }

    private UserEventDTO toDTO(User user) {
        return new UserEventDTO(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getImageUrl());
    }

    public void publishUserCreated(User user) {
        UserEventDTO dto = toDTO(user);

        Message<UserEventDTO> message = MessageBuilder.withPayload(dto)
                .setHeader("X-B3-TraceId", tracer.currentSpan().context().traceId())
                .setHeader("X-B3-SpanId", tracer.currentSpan().context().spanId())
                .build();
        kafkaTemplate.send("user.created", toDTO(user));
    }

    public void publishUserUpdated(User user) {
        UserEventDTO dto = toDTO(user);

        Message<UserEventDTO> message = MessageBuilder.withPayload(dto)
                .setHeader("X-B3-TraceId", tracer.currentSpan().context().traceId())
                .setHeader("X-B3-SpanId", tracer.currentSpan().context().spanId())
                .build();
        kafkaTemplate.send("user.updated", toDTO(user));
    }
}

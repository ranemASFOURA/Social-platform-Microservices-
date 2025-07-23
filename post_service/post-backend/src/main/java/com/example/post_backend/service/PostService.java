package com.example.post_backend.service;

import com.example.post_backend.model.Post;
import com.example.post_backend.repository.PostRepository;
import org.springframework.stereotype.Service;
import com.example.post_backend.kafka.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Span;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.time.Instant;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final KafkaProducer kafkaProducer;

    @Autowired
    private Tracer tracer;

    @Autowired
    private KafkaTemplate<String, Post> kafkaTemplate;

    public PostService(PostRepository postRepository, KafkaProducer kafkaProducer) {
        this.postRepository = postRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional
    public Post createPost(String userId, String caption, String imageUrl) {
        Post post = new Post(userId, caption, imageUrl, Instant.now());

        if (userId == null || imageUrl == null ||
                caption.isBlank() || imageUrl.isBlank()) {
            throw new IllegalArgumentException("Post fields must not be empty.");
        }
        Post saved = postRepository.save(post);

        kafkaProducer.sendPostCreatedEvent(
                saved.getId().toString(),
                saved.getUserId(),
                saved.getImageUrl(),
                saved.getCaption(),
                saved.getCreatedAt().toString());

        return saved;
    }

    public Page<Post> getUserPosts(String userId, Pageable pageable) {
        return postRepository.findByUserId(userId, pageable);
    }
}
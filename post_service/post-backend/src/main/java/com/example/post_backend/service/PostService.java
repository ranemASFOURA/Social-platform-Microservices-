package com.example.post_backend.service;

import com.example.post_backend.model.Post;
import com.example.post_backend.repository.PostRepository;
import org.springframework.stereotype.Service;
import com.example.post_backend.kafka.*;

import java.time.Instant;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final KafkaProducer kafkaProducer;

    public PostService(PostRepository postRepository, KafkaProducer kafkaProducer) {
        this.postRepository = postRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public Post createPost(String userId, String caption, String imageUrl) {
        Post post = new Post(userId, caption, imageUrl, Instant.now());
        Post saved = postRepository.save(post);

        kafkaProducer.sendPostCreatedEvent(
                saved.getId().toString(),
                saved.getUserId(),
                saved.getImageUrl(),
                saved.getCaption(),
                saved.getCreatedAt().toString());

        return saved;
    }

    public List<Post> getUserPosts(String userId) {
        return postRepository.findByUserId(userId);
    }
}

package com.example.post_backend.service;

import com.example.post_backend.model.Post;
import com.example.post_backend.repository.PostRepository;
import org.springframework.stereotype.Service;
import com.example.post_backend.kafka.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.time.Instant;
import java.util.Map;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final KafkaProducer kafkaProducer;
    private final RestTemplate restTemplate;
    private final ImageService imageService;

    @Value("${user.service.url}")
    private String userServiceUrl;

    public PostService(PostRepository postRepository,
            KafkaProducer kafkaProducer,
            RestTemplate restTemplate,
            ImageService imageService) {
        this.postRepository = postRepository;
        this.kafkaProducer = kafkaProducer;
        this.restTemplate = restTemplate;
        this.imageService = imageService;
    }

    @Transactional
    public Post createPost(String userId, String caption, String imageUrl) {
        Post post = new Post(userId, caption, imageUrl, Instant.now());

        if (userId == null || imageUrl == null ||
                caption.isBlank() || imageUrl.isBlank()) {
            throw new IllegalArgumentException("Post fields must not be empty.");
        }
        String userType = "regular"; // fallback

        try {
            String url = userServiceUrl + "/" + userId + "/type";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                userType = response.getBody().get("type").toString();
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch userType: " + e.getMessage());
        }

        Post saved = postRepository.save(post);

        kafkaProducer.sendPostCreatedEvent(
                saved.getId().toString(),
                saved.getUserId(),
                saved.getImageUrl(),
                saved.getCaption(),
                saved.getCreatedAt().toString(),
                userType);

        return saved;
    }

    public Page<Post> getUserPosts(String userId, Pageable pageable) {
        return postRepository.findByUserId(userId, pageable);
    }

    @Transactional
    public void deletePost(Long postId, String requesterUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(requesterUserId)) {
            throw new RuntimeException("Unauthorized to delete this post");
        }

        imageService.deleteImageByUrl(post.getImageUrl());

        postRepository.deleteById(postId);

        kafkaProducer.sendPostDeletedEvent(postId.toString(), requesterUserId);
    }

}
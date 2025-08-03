package com.example.post_backend;

import com.example.post_backend.model.Post;
import com.example.post_backend.repository.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void shouldFindPostsByUserId() {
        Post post1 = new Post("user123", "Caption 1", "url1", Instant.now());
        Post post2 = new Post("user123", "Caption 2", "url2", Instant.now());
        Post post3 = new Post("otherUser", "Caption 3", "url3", Instant.now());

        postRepository.saveAll(List.of(post1, post2, post3));

        var results = postRepository.findByUserId("user123", PageRequest.of(0, 10)).getContent();

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(p -> p.getUserId().equals("user123"));
    }
}

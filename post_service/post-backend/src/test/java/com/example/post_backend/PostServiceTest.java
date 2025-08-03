package com.example.post_backend;

import com.example.post_backend.model.Post;
import com.example.post_backend.repository.PostRepository;
import com.example.post_backend.service.PostService;
import com.example.post_backend.kafka.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @MockBean
    private KafkaProducer kafkaProducer;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void testCreatePostStoresPostAndSendsEvent() {
        // Arrange
        String userId = "userX";
        String caption = "Test caption";
        String imageUrl = "image.jpg";

        Map<String, String> mockResponse = Map.of("type", "regular");
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        // Act
        Post post = postService.createPost(userId, caption, imageUrl);

        // Assert
        List<Post> all = postRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getCaption()).isEqualTo(caption);

        verify(kafkaProducer, times(1))
                .sendPostCreatedEvent(any(), eq(userId), eq(imageUrl), eq(caption), any(), eq("regular"));
    }
}

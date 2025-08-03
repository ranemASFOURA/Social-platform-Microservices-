package com.example.post_backend;

import com.example.post_backend.controller.PostController;
import com.example.post_backend.service.PostService;
import com.example.post_backend.model.Post;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
public class PostControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Test
    void createPost_WithoutImageUrl_ShouldReturnBadRequest() throws Exception {
        Post post = new Post();
        post.setCaption("No image");
        // intentionally skip setting imageUrl

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        mockMvc.perform(post("/api/posts")
                .header("X-User-Id", "user123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(post)))
                .andExpect(status().isBadRequest());
    }
}

package com.example.post_backend;

import com.example.post_backend.controller.PostController;
import com.example.post_backend.service.PostService;
import com.example.post_backend.model.Post;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Test
    void testGetUserPosts() throws Exception {
        Post post = new Post("user123", "Caption", "url.jpg", Instant.now());
        when(postService.getUserPosts(Mockito.eq("user123"), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(post), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/posts/user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].caption").value("Caption"));
    }

    @Test
    void testCreatePost() throws Exception {
        Post inputPost = new Post();
        inputPost.setCaption("New caption");
        inputPost.setImageUrl("https://example.com/image.webp");

        Post returnedPost = new Post();
        returnedPost.setCaption("New caption");
        returnedPost.setImageUrl("https://example.com/image.webp");
        returnedPost.setUserId("user123");
        returnedPost.setCreatedAt(Instant.now());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // اضبط الموك ليُرجع كائن post كما لو أن الخدمة أرجعته
        when(postService.createPost("user123", "New caption", "https://example.com/image.webp"))
                .thenReturn(returnedPost);

        mockMvc.perform(post("/api/posts")
                .header("X-User-Id", "user123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(inputPost)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caption").value("New caption"));
    }

}

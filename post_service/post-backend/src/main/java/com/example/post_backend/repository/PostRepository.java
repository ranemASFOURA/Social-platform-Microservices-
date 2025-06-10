package com.example.post_backend.repository;

import com.example.post_backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserId(String userId, Pageable pageable);
}

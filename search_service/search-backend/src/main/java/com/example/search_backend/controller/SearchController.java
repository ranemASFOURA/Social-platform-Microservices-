package com.example.search_backend.controller;

import com.example.search_backend.model.UserDocument;
import com.example.search_backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    public SearchController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/firstname")
    public ResponseEntity<List<UserDocument>> searchByFirstname(@RequestParam String firstname) {
        logger.info("Received search request for firstname: {}", firstname);
        List<UserDocument> results = userRepository.findByFirstnameContainingIgnoreCase(firstname);
        return ResponseEntity.ok(results);
    }

}

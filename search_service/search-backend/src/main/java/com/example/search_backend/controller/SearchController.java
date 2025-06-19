package com.example.search_backend.controller;

import com.example.search_backend.model.UserDocument;
import com.example.search_backend.service.SearchService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/firstname")
    public ResponseEntity<List<UserDocument>> searchByFirstname(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam String firstname) {

        logger.info("Search requested by user: {}", userId);
        List<UserDocument> results = searchService.searchByFirstname(firstname);
        return ResponseEntity.ok(results);
    }

}

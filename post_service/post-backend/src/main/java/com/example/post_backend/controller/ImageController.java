package com.example.post_backend.controller;

import com.example.post_backend.service.ImageService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/generate-upload-url")
    public Map<String, String> generateUploadUrl(@RequestParam String filename) {
        return imageService.generateUploadUrl(filename);
    }
}

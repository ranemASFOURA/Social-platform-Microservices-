package com.example.user_backend.service;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ImageService {

    private final MinioClient minioClient;
    private static final String BUCKET_NAME = "user-images";

    public ImageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public Map<String, String> generateUploadData(String originalFilename) {
        try {
            String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String objectName = UUID.randomUUID() + extension;

            String uploadUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(BUCKET_NAME)
                            .object(objectName)
                            .expiry(10, TimeUnit.MINUTES)
                            .build());

            String fileUrl = buildFileUrl(objectName);

            Map<String, String> response = new HashMap<>();
            response.put("uploadUrl", uploadUrl);
            response.put("fileUrl", fileUrl);
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Error generating presigned URL", e);
        }
    }

    public String buildFileUrl(String objectName) {
        return "http://localhost:9000/" + BUCKET_NAME + "/" + objectName;
    }
}

package com.example.post_backend.service;

import io.minio.*;
import io.minio.http.Method;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ImageService {

    private final MinioClient minio;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.url}")
    private String internalUrl;
    @Value("${minio.public-url}")
    private String publicUrl;

    public ImageService(@Qualifier("publicMinio") MinioClient minio) {
        this.minio = minio;
    }

    public Map<String, String> generateUploadUrl(String originalFilename) {
        try {
            // String extension =
            // originalFilename.substring(originalFilename.lastIndexOf('.'));
            String objectName = UUID.randomUUID() + ".webp";

            String uploadUrl = minio.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
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

    private String buildFileUrl(String objectName) {
        return publicUrl + "/" + bucketName + "/" + objectName;

    }
}

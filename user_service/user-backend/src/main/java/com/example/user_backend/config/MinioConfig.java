package com.example.user_backend.config;

import io.minio.MinioClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Marks this class as a source of bean definitions for Spring context
@Configuration
public class MinioConfig {

    // @Value("${minio.url}")
    // private String url;

    // @Value("${minio.accessKey}")
    // private String accessKey;

    // @Value("${minio.secretKey}")
    // private String secretKey;
    // Define a MinIO client bean used internally inside the cluster
    @Bean("internalMinio")
    public MinioClient internal(@Value("${minio.url}") String url,
            @Value("${minio.accessKey}") String ak,
            @Value("${minio.secretKey}") String sk) {
        return MinioClient.builder().endpoint(url).credentials(ak, sk).build();
    }

    @Bean("publicMinio")
    public MinioClient external(@Value("${minio.public-url}") String url,
            @Value("${minio.accessKey}") String ak,
            @Value("${minio.secretKey}") String sk) {
        return MinioClient.builder().endpoint(url).credentials(ak, sk).build();
    }
}

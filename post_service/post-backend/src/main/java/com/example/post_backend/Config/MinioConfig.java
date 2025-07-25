package com.example.post_backend.Config;

import io.minio.MinioClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    // @Value("${minio.url}")
    // private String endpoint;

    // @Value("${minio.accessKey}")
    // private String accessKey;

    // @Value("${minio.secretKey}")
    // private String secretKey;

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

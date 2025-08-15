package com.tkachev.cloudfilestorage.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Value("${spring.minio.url}")
    private String minioUrl;

    @Value("${spring.minio.login}")
    private String minioLogin;

    @Value("${spring.minio.password}")
    private String minioPassword;
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(minioLogin, minioPassword)
                .build();
    }
}

package com.example.api_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final FileStorageProperties fileStorageProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String uploadDir = fileStorageProperties.getUploadDir();
        String urlPath = fileStorageProperties.getUrlPath();

        Path absolutePath = Paths.get(uploadDir).toAbsolutePath(); // <-- Quan trọng

        System.out.println("UPLOAD PATH = " + absolutePath);

        registry.addResourceHandler(urlPath)
                .addResourceLocations("file:" + absolutePath + "/");
    }
}

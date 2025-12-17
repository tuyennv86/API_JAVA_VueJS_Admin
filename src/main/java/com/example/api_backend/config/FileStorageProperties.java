package com.example.api_backend.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@ConfigurationProperties(prefix = "file")
@Getter
@Setter

public class FileStorageProperties {
    private String uploadDir;
    private String urlPath;
    private String maxSize;

    /**
     * Kiểm tra và khởi tạo thư mục upload khi ứng dụng khởi động
     */
    @PostConstruct
    public void validateAndCreateUploadDir() {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

            // Nếu thư mục chưa tồn tại → tạo mới
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("[FileStorageProperties] ✅ Đã tạo thư mục upload: " + uploadPath);
            }

            // Kiểm tra quyền ghi
            File dir = uploadPath.toFile();
            if (!dir.canWrite()) {
                throw new RuntimeException("❌ Không có quyền ghi vào thư mục upload: " + uploadPath);
            }

        } catch (Exception ex) {
            throw new RuntimeException("❌ Lỗi khi khởi tạo thư mục upload: " + uploadDir, ex);
        }
    }
}

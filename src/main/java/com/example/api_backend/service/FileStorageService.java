package com.example.api_backend.service;


import com.example.api_backend.config.FileStorageProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Service
public class FileStorageService {

    private final Path rootLocation;

    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.rootLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.rootLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo thư mục upload gốc", ex);
        }
    }


    //Lưu file vào thư mục theo ngày và tự đổi tên nếu trùng

    public String storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (originalFileName.contains("..")) {
            throw new RuntimeException("Tên file không hợp lệ: " + originalFileName);
        }

        // Tạo thư mục theo ngày
        String dateFolder = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Path dateDir = rootLocation.resolve(dateFolder);
        try {
            Files.createDirectories(dateDir);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục ngày: " + dateDir, e);
        }

        // Xử lý tên file để tránh trùng
        String fileName = resolveFileName(dateDir, originalFileName);
        Path targetLocation = dateDir.resolve(fileName);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu file: " + fileName, ex);
        }

        // Trả về đường dẫn tương đối: "20251113/tenfile.jpg"
        return dateFolder + "/" + fileName;
    }

    /**
     * Nếu file trùng, tự động thêm hậu tố: avatar_1.jpg, avatar_2.jpg...
     */
    private String resolveFileName(Path dir, String originalFileName) {
        String name = originalFileName;
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex > 0) {
            name = originalFileName.substring(0, dotIndex);
            extension = originalFileName.substring(dotIndex);
        }

        String fileName = originalFileName;
        int count = 1;
        while (Files.exists(dir.resolve(fileName))) {
            fileName = name + "_" + count + extension;
            count++;
        }
        return fileName;
    }

    /**
     * Xóa file cũ (đường dẫn tương đối)
     */
    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;
        try {
            Path filePath = this.rootLocation.resolve(Paths.get(relativePath)).normalize();
            Files.deleteIfExists(filePath);
            System.out.println("Đã xóa file: " + filePath);
        } catch (IOException ex) {
            System.err.println("Không thể xóa file: " + relativePath + " — " + ex.getMessage());
        }
    }
}

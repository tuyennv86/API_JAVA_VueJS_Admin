package com.example.api_backend.controller;

import com.example.api_backend.config.FileStorageProperties;
import com.example.api_backend.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("${app.api.prefix}/images")
@RequiredArgsConstructor
public class ImageController {

    private final FileStorageService fileStorageService;
    private final FileStorageProperties fileStorageProperties;

    @Value("${app.base-url:http://localhost:8888}")
    private String baseUrl;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml"
    );

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) return badRequest("File không được để trống");
        if (!ALLOWED_TYPES.contains(file.getContentType())) return badRequest("Chỉ chấp nhận file ảnh");
        try {
            String relativePath = fileStorageService.storeFile(file);
            String urlPrefix    = fileStorageProperties.getUrlPath().replace("/**", "");
            String imageUrl     = baseUrl + urlPrefix + "/" + relativePath;
            return ResponseEntity.ok(Map.of(
                    "success", true, "message", "Upload thành công",
                    "url", imageUrl, "fileName", relativePath
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false, "message", "Upload thất bại: " + e.getMessage()
            ));
        }
    }

    @RequestMapping(value = "/browser",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS, RequestMethod.PUT})
    public ResponseEntity<Map<String, Object>> browser(HttpServletRequest request) {
        String action = getParam(request, "action", "files");
        String name   = getParam(request, "name", "");
        String path   = getParam(request, "path", "").replaceAll("^/+", "");
        String from   = getParam(request, "from", "").replaceAll("^/+", "");

        System.out.printf("[Browser] %s action=%s path='%s' name='%s'%n",
                request.getMethod(), action, path, name);

        return switch (action) {
            // path rỗng → tất cả ảnh; path có giá trị → chỉ ảnh trong thư mục đó
            case "files"                  -> path.isBlank() ? listAllFiles() : listFilesInFolder(path);
            // Jodit gửi "fileRemove" hoặc "remove"
            case "fileRemove", "remove"   -> removeFile(name, path);
            // Jodit gửi "folderCreate" hoặc "createFolder"
            case "folderCreate", "createFolder" -> createFolder(name, path);
            case "move"                   -> moveFile(from, path);
            default                       -> listAllFiles();
        };
    }

    // ── Tất cả ảnh (đệ quy) — dùng khi path rỗng ─────────────────
    private ResponseEntity<Map<String, Object>> listAllFiles() {
        Path rootPath = getRoot();
        String baseurl = getBaseurl();

        List<Map<String, Object>> files   = new ArrayList<>();
        List<String>              folders = new ArrayList<>();

        if (Files.exists(rootPath)) {
            try {
                Files.walkFileTree(rootPath, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                        if (!dir.equals(rootPath)) {
                            String rel = rootPath.relativize(dir).toString().replace("\\", "/");
                            if (!rel.contains("/")) folders.add(rel); // chỉ cấp 1
                        }
                        return FileVisitResult.CONTINUE;
                    }
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        if (isImage(file.getFileName().toString())) {
                            String rel = rootPath.relativize(file).toString().replace("\\", "/");
                            files.add(buildFileEntry(rel, file.getFileName().toString(), attrs));
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                return joditError(e.getMessage());
            }
        }

        sortByChanged(files);
        System.out.printf("[Browser] ALL → %d files, %d folders%n", files.size(), folders.size());
        return ok(baseurl, "", files, folders);
    }

    // ── Chỉ ảnh trong 1 thư mục cụ thể — dùng khi click folder ───
    private ResponseEntity<Map<String, Object>> listFilesInFolder(String folderPath) {
        Path rootPath  = getRoot();
        Path targetDir = rootPath.resolve(folderPath).normalize();
        String baseurl = getBaseurl();

        if (!targetDir.startsWith(rootPath)) return badRequest("Đường dẫn không hợp lệ");

        List<Map<String, Object>> files   = new ArrayList<>();
        List<String>              folders = new ArrayList<>();

        if (Files.exists(targetDir)) {
            try (var stream = Files.list(targetDir)) {
                stream.sorted().forEach(entry -> {
                    try {
                        String name = entry.getFileName().toString();
                        if (Files.isDirectory(entry)) {
                            folders.add(name);
                        } else if (isImage(name)) {
                            BasicFileAttributes attrs = Files.readAttributes(entry, BasicFileAttributes.class);
                            // Jodit tính URL = baseurl + path + "/" + file
                            // path đã là "20260310" → file chỉ cần là tên file, không lặp path
                            files.add(buildFileEntry(name, name, attrs));
                        }
                    } catch (IOException ignored) {}
                });
            } catch (IOException e) {
                return joditError(e.getMessage());
            }
        }

        sortByChanged(files);
        System.out.printf("[Browser] FOLDER '%s' → %d files%n", folderPath, files.size());
        return ok(baseurl, folderPath, files, folders);
    }

    private ResponseEntity<Map<String, Object>> ok(
            String baseurl, String path,
            List<Map<String, Object>> files, List<String> folders) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "time",    Instant.now().toString(),
                "data", Map.of(
                        "sources", List.of(Map.of(
                                "name",    "uploads",
                                "title",   "Uploads",
                                "baseurl", baseurl,
                                "path",    path,
                                "files",   files,
                                "folders", folders
                        )),
                        "code", 220, "message", ""
                )
        ));
    }

    private Map<String, Object> buildFileEntry(String rel, String name, BasicFileAttributes attrs) {
        return Map.of(
                "file",    rel,
                "name",    name,
                "thumb",   rel,
                "changed", String.valueOf(attrs.lastModifiedTime().toInstant().getEpochSecond()),
                "size",    formatSize(attrs.size()),
                "isImage", true,
                "type",    "image"
        );
    }

    private void sortByChanged(List<Map<String, Object>> files) {
        files.sort((a, b) -> b.get("changed").toString().compareTo(a.get("changed").toString()));
    }

    private ResponseEntity<Map<String, Object>> createFolder(String name, String path) {
        if (name == null || name.isBlank() || name.contains("..") || name.contains("/"))
            return badRequest("Tên thư mục không hợp lệ");
        Path rootPath  = getRoot();
        String clean   = path != null ? path.replaceAll("^/+", "") : "";
        Path parentDir = clean.isBlank() ? rootPath : rootPath.resolve(clean).normalize();
        if (!parentDir.startsWith(rootPath)) return badRequest("Đường dẫn không hợp lệ");
        try {
            Files.createDirectories(parentDir.resolve(name));
            return clean.isBlank() ? listAllFiles() : listFilesInFolder(clean);
        } catch (IOException e) {
            return joditError(e.getMessage());
        }
    }

    private ResponseEntity<Map<String, Object>> removeFile(String name, String path) {
        if (name == null || name.isBlank() || name.contains("..")) return badRequest("Tên không hợp lệ");
        Path rootPath = getRoot();
        String burl   = getBaseurl();

        // Jodit gửi: name="lau_cua2_2.jpg", path="20260309"
        // → cần ghép thành "20260309/lau_cua2_2.jpg"
        String rel;
        if (name.startsWith(burl)) {
            // Trường hợp name là full URL
            rel = name.substring(burl.length());
        } else if (name.contains("/")) {
            // name đã có path rồi (20260309/lau_cua2_2.jpg)
            rel = name;
        } else if (path != null && !path.isBlank()) {
            // name thuần + path riêng → ghép lại
            rel = path + "/" + name;
        } else {
            rel = name;
        }

        rel = rel.replaceAll("^/+", "");
        Path target = rootPath.resolve(rel).normalize();

        System.out.printf("[Browser] removeFile rel='%s' target='%s'%n", rel, target);

        if (!target.startsWith(rootPath)) return badRequest("Đường dẫn không hợp lệ");
        try {
            if (Files.isDirectory(target)) {
                Files.walk(target).sorted(Comparator.reverseOrder())
                        .forEach(p -> { try { Files.delete(p); } catch (IOException ignored) {} });
            } else {
                Files.deleteIfExists(target);
            }
            return listAllFiles();
        } catch (IOException e) {
            return joditError(e.getMessage());
        }
    }

    private ResponseEntity<Map<String, Object>> moveFile(String from, String toPath) {
        if (from == null || from.isBlank()) return listAllFiles();
        Path rootPath  = getRoot();
        Path source    = rootPath.resolve(from.replaceAll("^/+", "")).normalize();
        String cleanTo = toPath != null ? toPath.replaceAll("^/+", "") : "";
        Path targetDir = cleanTo.isBlank() ? rootPath : rootPath.resolve(cleanTo).normalize();
        if (!source.startsWith(rootPath) || !targetDir.startsWith(rootPath)) return badRequest("Không hợp lệ");
        try {
            Files.createDirectories(targetDir);
            Files.move(source, targetDir.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            return listAllFiles();
        } catch (IOException e) {
            return joditError(e.getMessage());
        }
    }

    private Path   getRoot()    { return Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize(); }
    private String getBaseurl() { return baseUrl + fileStorageProperties.getUrlPath().replace("/**", "") + "/"; }

    private String getParam(HttpServletRequest req, String key, String def) {
        String v = req.getParameter(key); return v != null ? v : def;
    }

    private boolean isImage(String n) {
        String l = n.toLowerCase();
        return l.endsWith(".jpg")||l.endsWith(".jpeg")||l.endsWith(".png")
                ||l.endsWith(".gif")||l.endsWith(".webp")||l.endsWith(".svg");
    }

    private String formatSize(long b) {
        if (b < 1024) return b + " B";
        if (b < 1024*1024) return (b/1024) + " KB";
        return String.format("%.1f MB", b/(1024.0*1024));
    }

    private ResponseEntity<Map<String, Object>> badRequest(String msg) {
        return ResponseEntity.badRequest().body(Map.of("success", false,
                "time", Instant.now().toString(),
                "data", Map.of("sources", List.of(), "code", 400, "message", msg)));
    }

    private ResponseEntity<Map<String, Object>> joditError(String msg) {
        return ResponseEntity.internalServerError().body(Map.of("success", false,
                "time", Instant.now().toString(),
                "data", Map.of("sources", List.of(), "code", 500, "message", msg)));
    }
}
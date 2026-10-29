package com.birbuketmutluluk.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.birbuketmutluluk.exception.ApiException;

import jakarta.annotation.PostConstruct;

/**
 * Görselleri sunucudaki yerel diske kaydeder ('uploads/' klasörü, yapılandırılabilir).
 * Dosya adı sunucuda UUID ile üretilir; istemci adı kullanılmaz (path traversal engeli).
 */
@Service
public class LocalStorageService implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalStorageService.class);

    /** İzin verilen içerik tipi -> uzantı. */
    private static final Map<String, String> ALLOWED = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp");

    private final String uploadDir;
    private Path root;

    public LocalStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @PostConstruct
    void init() {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
            log.info("Görsel yükleme klasörü: {}", root);
        } catch (IOException e) {
            throw new IllegalStateException("Yükleme klasörü oluşturulamadı: " + root, e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Yüklenecek dosya boş.");
        }

        String extension = ALLOWED.get(file.getContentType());
        if (extension == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Yalnızca JPG, PNG veya WEBP görselleri yüklenebilir.");
        }

        String filename = UUID.randomUUID().toString().replace("-", "") + extension;
        Path target = root.resolve(filename).normalize();

        // Güvenlik: hedef kök klasörün dışına çıkamaz.
        if (!target.getParent().equals(root)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Geçersiz dosya yolu.");
        }

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Dosya kaydedilemedi: {}", e.getMessage(), e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Dosya kaydedilemedi.");
        }

        // İstekten public URL üret (dev: http://localhost:8080/uploads/..., prod: API domaini)
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(filename)
                .toUriString();
    }
}

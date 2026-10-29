package com.birbuketmutluluk.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Görsel depolama soyutlaması. Lokal disk uygulaması vardır;
 * ileride Cloudinary/S3 uygulaması bu arayüzü karşılayarak eklenebilir.
 */
public interface StorageService {

    /**
     * Dosyayı doğrular, kaydeder ve erişilebilir public URL döner.
     */
    String store(MultipartFile file);
}

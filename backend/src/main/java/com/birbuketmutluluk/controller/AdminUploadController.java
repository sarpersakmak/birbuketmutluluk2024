package com.birbuketmutluluk.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.birbuketmutluluk.dto.UploadResponse;
import com.birbuketmutluluk.service.StorageService;

@RestController
@RequestMapping("/api/admin/uploads")
public class AdminUploadController {

    private final StorageService storageService;

    public AdminUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping
    public UploadResponse upload(@RequestParam("file") MultipartFile file) {
        return new UploadResponse(storageService.store(file));
    }
}

package com.birbuketmutluluk.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.birbuketmutluluk.dto.AdminUserDetailResponse;
import com.birbuketmutluluk.dto.AdminUserResponse;
import com.birbuketmutluluk.dto.UpdateUserStatusRequest;
import com.birbuketmutluluk.service.AdminUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public List<AdminUserResponse> list(@RequestParam(required = false) String q) {
        return adminUserService.listUsers(q);
    }

    @GetMapping("/{id}")
    public AdminUserDetailResponse detail(@PathVariable Long id) {
        return adminUserService.getUser(id);
    }

    @PutMapping("/{id}/status")
    public AdminUserResponse updateStatus(Authentication authentication,
                                          @PathVariable Long id,
                                          @Valid @RequestBody UpdateUserStatusRequest request) {
        return adminUserService.updateStatus(authentication.getName(), id, request.active());
    }
}

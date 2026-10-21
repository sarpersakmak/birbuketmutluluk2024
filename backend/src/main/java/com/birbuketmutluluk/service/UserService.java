package com.birbuketmutluluk.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.birbuketmutluluk.dto.ChangePasswordRequest;
import com.birbuketmutluluk.dto.UpdateProfileRequest;
import com.birbuketmutluluk.dto.UserResponse;
import com.birbuketmutluluk.entity.User;
import com.birbuketmutluluk.exception.ApiException;
import com.birbuketmutluluk.repository.UserRepository;

/**
 * Kullanıcının kendi hesabını yönetmesi (bölüm 14).
 * E-posta ve rol bu servisle DEĞİŞMEZ.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = resolve(email);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());
        return UserResponse.from(user);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = resolve(email);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Mevcut şifreniz hatalı.");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
    }

    private User resolve(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Kullanıcı bulunamadı."));
    }
}

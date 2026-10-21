package com.birbuketmutluluk.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.birbuketmutluluk.dto.AuthResponse;
import com.birbuketmutluluk.dto.LoginRequest;
import com.birbuketmutluluk.dto.RegisterRequest;
import com.birbuketmutluluk.dto.UserResponse;
import com.birbuketmutluluk.entity.Role;
import com.birbuketmutluluk.entity.User;
import com.birbuketmutluluk.exception.ApiException;
import com.birbuketmutluluk.repository.UserRepository;
import com.birbuketmutluluk.security.JwtService;

/**
 * Kimlik doğrulama iş mantığı (bölüm 9). İş kuralları controller'da değil burada tutulur (Kural 14).
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Bu e-posta zaten kayıtlı.");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .role(Role.USER)
                .active(true)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, UserResponse.from(user));
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (DisabledException ex) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Hesabınız pasif durumda. Lütfen yönetici ile iletişime geçin.");
        } catch (AuthenticationException ex) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "E-posta veya şifre hatalı.");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "E-posta veya şifre hatalı."));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, UserResponse.from(user));
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı."));
        return UserResponse.from(user);
    }
}

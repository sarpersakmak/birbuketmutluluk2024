package com.birbuketmutluluk.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.birbuketmutluluk.entity.Role;
import com.birbuketmutluluk.entity.User;
import com.birbuketmutluluk.repository.UserRepository;

/**
 * Geliştirme kolaylığı için başlangıç ADMIN kullanıcısı oluşturur (bölüm 32).
 *
 * E-posta/şifre ortam değişkeninden gelir (ADMIN_EMAIL / ADMIN_PASSWORD);
 * tanımlı değilse dev amaçlı varsayılanlar kullanılır. Production şifresi
 * KESİNLİKLE kaynak koda konmaz — ortam değişkeni ile verilir.
 *
 * Admin zaten varsa tekrar oluşturmaz.
 */
@Configuration
@Profile("dev")
public class AdminSeeder {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                @Value("${app.admin.email}") String adminEmail,
                                @Value("${app.admin.password}") String adminPassword) {
        return args -> {
            if (userRepository.existsByEmail(adminEmail)) {
                log.info("ADMIN kullanıcısı zaten mevcut: {}", adminEmail);
                return;
            }

            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("Kullanici")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .active(true)
                    .build();

            userRepository.save(admin);
            log.info("Varsayilan ADMIN olusturuldu -> e-posta: {} (sifreyi uretimde ADMIN_PASSWORD ile degistirin)",
                    adminEmail);
        };
    }
}

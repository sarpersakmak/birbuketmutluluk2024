package com.birbuketmutluluk.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.birbuketmutluluk.entity.Role;
import com.birbuketmutluluk.entity.User;
import com.birbuketmutluluk.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Uçtan uca güvenlik/auth entegrasyon testleri (H2).
 * Seeder'lar yalnızca "dev" profilinde çalıştığı için test profilinde DB boştur;
 * gerekli kullanıcılar test içinde oluşturulur.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthSecurityIntegrationTest {

    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    private HttpHeaders json() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    private HttpHeaders bearer(String token) {
        HttpHeaders h = json();
        h.setBearerAuth(token);
        return h;
    }

    @Test
    void register_login_me_akisi_calisir() {
        String body = """
                {"firstName":"Ayse","lastName":"Y","email":"ayse@test.com","password":"sifre1234"}
                """;
        ResponseEntity<Map> reg = rest.postForEntity("/api/auth/register",
                new HttpEntity<>(body, json()), Map.class);
        assertThat(reg.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(reg.getBody()).containsKey("token");

        String login = """
                {"email":"ayse@test.com","password":"sifre1234"}
                """;
        ResponseEntity<Map> log = rest.postForEntity("/api/auth/login",
                new HttpEntity<>(login, json()), Map.class);
        assertThat(log.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token = (String) log.getBody().get("token");
        assertThat(token).isNotBlank();

        ResponseEntity<Map> me = rest.exchange("/api/auth/me", HttpMethod.GET,
                new HttpEntity<>(bearer(token)), Map.class);
        assertThat(me.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(me.getBody().get("email")).isEqualTo("ayse@test.com");
        // Hassas alan dönmemeli
        assertThat(me.getBody()).doesNotContainKey("password");
    }

    @Test
    void adminUcu_tokensiz_401() {
        ResponseEntity<String> res = rest.getForEntity("/api/admin/dashboard", String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void adminUcu_userTokeni_403() {
        rest.postForEntity("/api/auth/register",
                new HttpEntity<>("""
                        {"firstName":"U","lastName":"Ser","email":"user@test.com","password":"sifre1234"}
                        """, json()), Map.class);
        ResponseEntity<Map> log = rest.postForEntity("/api/auth/login",
                new HttpEntity<>("""
                        {"email":"user@test.com","password":"sifre1234"}
                        """, json()), Map.class);
        String token = (String) log.getBody().get("token");

        ResponseEntity<String> res = rest.exchange("/api/admin/dashboard", HttpMethod.GET,
                new HttpEntity<>(bearer(token)), String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void adminUcu_adminTokeni_200() {
        userRepository.save(User.builder()
                .firstName("Admin").lastName("Test").email("admin@test.com")
                .password(passwordEncoder.encode("sifre1234"))
                .role(Role.ADMIN).active(true).build());

        ResponseEntity<Map> log = rest.postForEntity("/api/auth/login",
                new HttpEntity<>("""
                        {"email":"admin@test.com","password":"sifre1234"}
                        """, json()), Map.class);
        String token = (String) log.getBody().get("token");

        ResponseEntity<Map> res = rest.exchange("/api/admin/dashboard", HttpMethod.GET,
                new HttpEntity<>(bearer(token)), Map.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).containsKey("totalUsers");
    }
}

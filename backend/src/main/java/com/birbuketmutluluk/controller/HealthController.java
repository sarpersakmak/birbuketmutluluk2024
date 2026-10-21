package com.birbuketmutluluk.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Aşama 1 doğrulama ucu.
 *
 * Uygulamanın ayağa kalktığını ve REST katmanının çalıştığını teyit etmek için
 * basit bir sağlık kontrolü sağlar. İleriki aşamalarda iş uçları eklendikçe
 * bu uç kalabilir veya Actuator ile değiştirilebilir.
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "app", "bir-buket-mutluluk-backend"
        );
    }
}

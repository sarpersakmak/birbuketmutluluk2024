package com.birbuketmutluluk.config;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;

import com.birbuketmutluluk.entity.District;
import com.birbuketmutluluk.entity.Province;
import com.birbuketmutluluk.repository.DistrictRepository;
import com.birbuketmutluluk.repository.ProvinceRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * İl/ilçe referans verisini (resources/data/turkey-locations.json) veritabanına yükler.
 * Tüm profillerde çalışır; tablo boşsa bir kez doldurur.
 */
@Configuration
public class LocationSeeder {

    private static final Logger log = LoggerFactory.getLogger(LocationSeeder.class);

    @Bean
    @Order(1)
    CommandLineRunner seedLocations(ProvinceRepository provinceRepository,
                                    DistrictRepository districtRepository,
                                    ObjectMapper objectMapper) {
        return args -> {
            ClassPathResource resource = new ClassPathResource("data/turkey-locations.json");
            Map<String, List<String>> data = objectMapper.readValue(
                    resource.getInputStream(), new TypeReference<Map<String, List<String>>>() {
                    });

            int added = 0;
            // İdempotent: yalnızca eksik illeri ekler (var olanlara dokunmaz).
            for (Map.Entry<String, List<String>> entry : data.entrySet()) {
                if (provinceRepository.existsByName(entry.getKey())) {
                    continue;
                }
                Province province = provinceRepository.save(Province.builder().name(entry.getKey()).build());
                List<District> districts = entry.getValue().stream()
                        .map(name -> District.builder().province(province).name(name).build())
                        .toList();
                districtRepository.saveAll(districts);
                added++;
            }

            if (added > 0) {
                log.info("Il/ilce verisi: {} eksik il eklendi. Toplam il: {}", added, provinceRepository.count());
            }
        };
    }
}

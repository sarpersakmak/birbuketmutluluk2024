package com.birbuketmutluluk.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.birbuketmutluluk.repository.DistrictRepository;
import com.birbuketmutluluk.repository.ProvinceRepository;

/**
 * İl ve ilçe referans verisi (public). Adres formu bu uçlardan beslenir.
 */
@RestController
public class LocationController {

    public record LocationDto(Long id, String name) {
    }

    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;

    public LocationController(ProvinceRepository provinceRepository, DistrictRepository districtRepository) {
        this.provinceRepository = provinceRepository;
        this.districtRepository = districtRepository;
    }

    @GetMapping("/api/provinces")
    public List<LocationDto> provinces() {
        return provinceRepository.findAllByOrderByNameAsc().stream()
                .map(p -> new LocationDto(p.getId(), p.getName()))
                .toList();
    }

    @GetMapping("/api/provinces/{provinceId}/districts")
    public List<LocationDto> districts(@PathVariable Long provinceId) {
        return districtRepository.findByProvinceIdOrderByNameAsc(provinceId).stream()
                .map(d -> new LocationDto(d.getId(), d.getName()))
                .toList();
    }
}

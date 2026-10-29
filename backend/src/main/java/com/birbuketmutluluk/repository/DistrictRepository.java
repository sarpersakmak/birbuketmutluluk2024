package com.birbuketmutluluk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.birbuketmutluluk.entity.District;

public interface DistrictRepository extends JpaRepository<District, Long> {

    List<District> findByProvinceIdOrderByNameAsc(Long provinceId);
}

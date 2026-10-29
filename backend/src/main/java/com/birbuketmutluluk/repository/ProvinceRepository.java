package com.birbuketmutluluk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.birbuketmutluluk.entity.Province;

public interface ProvinceRepository extends JpaRepository<Province, Long> {

    List<Province> findAllByOrderByNameAsc();

    boolean existsByName(String name);
}

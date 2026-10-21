package com.birbuketmutluluk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.birbuketmutluluk.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserIdOrderByIsDefaultDescIdAsc(Long userId);

    Optional<Address> findByIdAndUserId(Long id, Long userId);
}

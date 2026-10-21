package com.birbuketmutluluk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.birbuketmutluluk.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByActiveTrue();

    List<User> findAllByOrderByIdAsc();

    @Query("""
            select u from User u
            where lower(u.firstName) like lower(concat('%', :q, '%'))
               or lower(u.lastName) like lower(concat('%', :q, '%'))
               or lower(u.email) like lower(concat('%', :q, '%'))
            order by u.id
            """)
    List<User> search(@Param("q") String q);
}

package com.example.ticketback.repository;

import com.example.ticketback.domain.entity.User;
import lombok.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité User.
 * JpaRepository fournit déjà :
 * - findById
 * - findAll
 * - save
 * - delete
 * - count
 * - pagination
 */

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    @NullMarked
    Optional<User> findById(Long id);

    /**
     * Évite d'utiliser findAll qui requête toute la liste
     */
    @Query("select u from User u order by u.id asc")
    List<User> findList(Pageable pageable);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}

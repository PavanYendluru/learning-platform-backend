package com.learningplatform.backend.repository;

import com.learningplatform.backend.entity.Domain;
import com.learningplatform.backend.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Database access layer for Domain.
 * Spring Data JPA automatically implements the methods.
 */
public interface DomainRepository extends JpaRepository<Domain, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlugIgnoreCase(String slug);

    Optional<Domain> findBySlugIgnoreCase(String slug);

    List<Domain> findByStatusOrderByNameAsc(Status status);
}
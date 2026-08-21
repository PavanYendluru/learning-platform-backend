package com.learningplatform.backend.repository;

import com.learningplatform.backend.entity.Topic;
import com.learningplatform.backend.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findByDomainIdAndStatusOrderBySequenceNumberAsc(Long domainId, Status status);

    List<Topic> findByDomainIdOrderBySequenceNumberAsc(Long domainId);

    boolean existsByDomainIdAndSlugIgnoreCase(Long domainId, String slug);

    Optional<Topic> findByIdAndDomainId(Long id, Long domainId);
}
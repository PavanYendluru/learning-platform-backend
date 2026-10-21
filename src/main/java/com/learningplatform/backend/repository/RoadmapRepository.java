package com.learningplatform.backend.repository;

import com.learningplatform.backend.entity.Roadmap;
import com.learningplatform.backend.enums.RoadmapStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
    List<Roadmap> findByStatusOrderByTitleAsc(RoadmapStatus status);
    List<Roadmap> findByDomainIdAndStatus(Long domainId, RoadmapStatus status);
    List<Roadmap> findByDomainId(Long domainId);
    Optional<Roadmap> findFirstByDomainIdAndStatus(Long domainId, RoadmapStatus status);
    Optional<Roadmap> findFirstByDomainIdOrderByCreatedAtDesc(Long domainId);
    boolean existsByDomainId(Long domainId);
}

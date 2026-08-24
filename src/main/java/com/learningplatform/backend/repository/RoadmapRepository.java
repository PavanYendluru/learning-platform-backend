// RoadmapRepository.java
package com.learningplatform.backend.repository;

import com.learningplatform.backend.entity.Roadmap;
import com.learningplatform.backend.enums.RoadmapStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
    List<Roadmap> findByStatusOrderByTitleAsc(RoadmapStatus status);
    List<Roadmap> findByDomainIdAndStatus(Long domainId, RoadmapStatus status);
}
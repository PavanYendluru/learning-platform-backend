package com.learningplatform.backend.repository;

import com.learningplatform.backend.entity.TechnologyUpdate;
import com.learningplatform.backend.enums.TechnologyUpdateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TechnologyUpdateRepository extends JpaRepository<TechnologyUpdate, Long> {

    List<TechnologyUpdate> findByStatusOrderByPublishedDateDesc(TechnologyUpdateStatus status);

    List<TechnologyUpdate> findByDomainIdAndStatusOrderByPublishedDateDesc(Long domainId, TechnologyUpdateStatus status);
}
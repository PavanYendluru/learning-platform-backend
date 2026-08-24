// RoadmapStepRepository.java
package com.learningplatform.backend.repository;

import com.learningplatform.backend.entity.RoadmapStep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapStepRepository extends JpaRepository<RoadmapStep, Long> {
}
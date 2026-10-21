package com.learningplatform.backend.repository;

import com.learningplatform.backend.entity.RoadmapStep;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoadmapStepRepository extends JpaRepository<RoadmapStep, Long> {
    List<RoadmapStep> findByTopicId(Long topicId);
    List<RoadmapStep> findByRoadmapIdOrderByStepOrderAsc(Long roadmapId);
    void deleteByTopicId(Long topicId);
    void deleteByRoadmapId(Long roadmapId);
}

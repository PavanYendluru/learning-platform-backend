package com.learningplatform.backend.repository;

import com.learningplatform.backend.entity.Resource;
import com.learningplatform.backend.enums.ResourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findByStatusOrderByCreatedAtDesc(ResourceStatus status);

    List<Resource> findByTopicIdAndStatusOrderByCreatedAtDesc(
            Long topicId,
            ResourceStatus status
    );
}
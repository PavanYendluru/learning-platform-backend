package com.learningplatform.backend.repository;

import com.learningplatform.backend.entity.Resource;
import com.learningplatform.backend.enums.ResourceStatus;
import com.learningplatform.backend.enums.ResourceType;
import com.learningplatform.backend.enums.ResourceVerificationStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Page<Resource> findByStatus(ResourceStatus status, Pageable pageable);

    Page<Resource> findByTopicIdAndStatus(Long topicId, ResourceStatus status, Pageable pageable);

    long countByStatus(ResourceStatus status);
    long countByVerificationStatus(ResourceVerificationStatus status);

    @Query("""
        SELECT r FROM Resource r
        WHERE (:status IS NULL OR r.status = :status)
          AND (:topicId IS NULL OR r.topic.id = :topicId)
          AND (:resourceType IS NULL OR r.resourceType = :resourceType)
          AND (:difficulty IS NULL OR LOWER(r.difficulty) = LOWER(:difficulty))
          AND (:publishedHierarchy = false OR
               (r.topic.status = com.learningplatform.backend.enums.Status.ACTIVE
                AND r.topic.domain.status = com.learningplatform.backend.enums.Status.ACTIVE))
          AND (
                :keyword IS NULL OR
                LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(r.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
        """)
    Page<Resource> search(
            @Param("status") ResourceStatus status,
            @Param("topicId") Long topicId,
            @Param("resourceType") ResourceType resourceType,
            @Param("difficulty") String difficulty,
            @Param("keyword") String keyword,
            @Param("publishedHierarchy") boolean publishedHierarchy,
            Pageable pageable
    );
}

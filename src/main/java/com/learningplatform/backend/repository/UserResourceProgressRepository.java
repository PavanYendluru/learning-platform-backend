package com.learningplatform.backend.repository;

import com.learningplatform.backend.entity.UserResourceProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserResourceProgressRepository extends JpaRepository<UserResourceProgress, Long> {

    Optional<UserResourceProgress> findByUserIdAndResourceId(Long userId, Long resourceId);

    boolean existsByUserIdAndResourceIdAndCompletedTrue(Long userId, Long resourceId);

    List<UserResourceProgress> findByUserIdAndCompletedTrue(Long userId);

    @Query("SELECT urp.resource.id FROM UserResourceProgress urp WHERE urp.user.id = :userId AND urp.completed = true")
    List<Long> findCompletedResourceIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(urp) FROM UserResourceProgress urp WHERE urp.user.id = :userId AND urp.completed = true AND urp.resource.topic.id = :topicId")
    long countCompletedByUserIdAndTopicId(@Param("userId") Long userId, @Param("topicId") Long topicId);

    @Query("SELECT COUNT(urp) FROM UserResourceProgress urp WHERE urp.user.id = :userId AND urp.completed = true AND urp.resource.topic.domain.id = :domainId")
    long countCompletedByUserIdAndDomainId(@Param("userId") Long userId, @Param("domainId") Long domainId);

    void deleteByResourceId(Long resourceId);

    void deleteByUserIdAndResourceId(Long userId, Long resourceId);
}

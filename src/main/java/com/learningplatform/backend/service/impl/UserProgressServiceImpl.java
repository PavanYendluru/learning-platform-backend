package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.dto.DashboardDataResponse;
import com.learningplatform.backend.dto.DomainProgressResponse;
import com.learningplatform.backend.dto.UserProgressRequest;
import com.learningplatform.backend.dto.UserProgressResponse;
import com.learningplatform.backend.entity.*;
import com.learningplatform.backend.enums.ResourceStatus;
import com.learningplatform.backend.enums.Status;
import com.learningplatform.backend.exception.DomainNotFoundException;
import com.learningplatform.backend.exception.ResourceNotFoundException;
import com.learningplatform.backend.exception.TopicNotFoundException;
import com.learningplatform.backend.repository.*;
import com.learningplatform.backend.service.CurrentUserService;
import com.learningplatform.backend.service.UserProgressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserProgressServiceImpl implements UserProgressService {

    private final UserProgressRepository progressRepository;
    private final UserResourceProgressRepository userResourceProgressRepository;
    private final TopicRepository topicRepository;
    private final ResourceRepository resourceRepository;
    private final DomainRepository domainRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CurrentUserService currentUserService;

    public UserProgressServiceImpl(UserProgressRepository progressRepository,
                                   UserResourceProgressRepository userResourceProgressRepository,
                                   TopicRepository topicRepository,
                                   ResourceRepository resourceRepository,
                                   DomainRepository domainRepository,
                                   BookmarkRepository bookmarkRepository,
                                   CurrentUserService currentUserService) {
        this.progressRepository = progressRepository;
        this.userResourceProgressRepository = userResourceProgressRepository;
        this.topicRepository = topicRepository;
        this.resourceRepository = resourceRepository;
        this.domainRepository = domainRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public UserProgressResponse saveProgress(Long topicId, UserProgressRequest request) {
        User user = currentUserService.getCurrentUser();
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException(topicId));

        UserProgress progress = progressRepository.findByUserIdAndTopicId(user.getId(), topicId)
                .orElseGet(() -> {
                    UserProgress p = new UserProgress();
                    p.setUser(user);
                    p.setTopic(topic);
                    return p;
                });

        progress.setCompleted(Boolean.TRUE.equals(request.getCompleted()));
        progress.setProgressPercentage(request.getProgressPercentage() != null ? request.getProgressPercentage() : 0);

        UserProgress saved = progressRepository.save(progress);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProgressResponse> getMyProgress() {
        User user = currentUserService.getCurrentUser();
        return progressRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserProgressResponse completeResource(Long resourceId) {
        User user = currentUserService.getCurrentUser();
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));

        UserResourceProgress urp = userResourceProgressRepository.findByUserIdAndResourceId(user.getId(), resourceId)
                .orElseGet(() -> {
                    UserResourceProgress p = new UserResourceProgress();
                    p.setUser(user);
                    p.setResource(resource);
                    return p;
                });

        urp.setCompleted(true);
        urp.setCompletedAt(LocalDateTime.now());
        userResourceProgressRepository.save(urp);

        return recalculateTopicProgress(user, resource.getTopic());
    }

    @Override
    public UserProgressResponse uncompleteResource(Long resourceId) {
        User user = currentUserService.getCurrentUser();
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));

        userResourceProgressRepository.findByUserIdAndResourceId(user.getId(), resourceId)
                .ifPresent(urp -> {
                    urp.setCompleted(false);
                    userResourceProgressRepository.save(urp);
                });

        return recalculateTopicProgress(user, resource.getTopic());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getCompletedResourceIds() {
        User user = currentUserService.getCurrentUser();
        return userResourceProgressRepository.findCompletedResourceIdsByUserId(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public DomainProgressResponse getDomainProgress(Long domainId) {
        User user = currentUserService.getCurrentUser();
        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new DomainNotFoundException(domainId));

        List<Topic> topics = topicRepository.findByDomainIdAndStatusOrderBySequenceNumberAsc(domainId, Status.ACTIVE);
        long totalTopics = topics.size();
        long completedTopics = 0;
        int totalPercentage = 0;

        for (Topic topic : topics) {
            UserProgress up = progressRepository.findByUserIdAndTopicId(user.getId(), topic.getId()).orElse(null);
            if (up != null) {
                if (up.isCompleted()) {
                    completedTopics++;
                }
                totalPercentage += (up.getProgressPercentage() != null ? up.getProgressPercentage() : 0);
            }
        }

        int avgPercentage = totalTopics > 0 ? Math.round((float) totalPercentage / totalTopics) : 0;
        long totalResources = resourceRepository.countByTopicDomainIdAndStatus(domainId, ResourceStatus.PUBLISHED);
        long completedResources = userResourceProgressRepository.countCompletedByUserIdAndDomainId(user.getId(), domainId);

        return new DomainProgressResponse(domain.getId(), domain.getName(), avgPercentage,
                completedTopics, totalTopics, completedResources, totalResources);
    }

    private UserProgressResponse recalculateTopicProgress(User user, Topic topic) {
        long totalPublished = resourceRepository.countByTopicIdAndStatus(topic.getId(), ResourceStatus.PUBLISHED);
        if (totalPublished == 0) {
            totalPublished = resourceRepository.countByTopicId(topic.getId());
        }

        long completedInTopic = userResourceProgressRepository.countCompletedByUserIdAndTopicId(user.getId(), topic.getId());
        int percentage = totalPublished > 0 ? (int) Math.min(100, Math.round((completedInTopic * 100.0) / totalPublished)) : 100;
        boolean isDone = (completedInTopic >= totalPublished && totalPublished > 0);

        UserProgress progress = progressRepository.findByUserIdAndTopicId(user.getId(), topic.getId())
                .orElseGet(() -> {
                    UserProgress p = new UserProgress();
                    p.setUser(user);
                    p.setTopic(topic);
                    return p;
                });

        progress.setProgressPercentage(percentage);
        progress.setCompleted(isDone);

        UserProgress saved = progressRepository.save(progress);
        return mapToResponse(saved);
    }

    private UserProgressResponse mapToResponse(UserProgress p) {
        UserProgressResponse r = new UserProgressResponse();
        r.setId(p.getId());
        r.setTopicId(p.getTopic().getId());
        r.setTopicName(p.getTopic().getName());
        if (p.getTopic().getDomain() != null) {
            r.setDomainId(p.getTopic().getDomain().getId());
            r.setDomainName(p.getTopic().getDomain().getName());
        }
        r.setCompleted(p.isCompleted());
        r.setProgressPercentage(p.getProgressPercentage());
        r.setCompletedAt(p.getCompletedAt());
        r.setUpdatedAt(p.getUpdatedAt());
        return r;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardDataResponse getDashboardData() {
        User user = currentUserService.getCurrentUser();
        List<UserProgress> userProgressList = progressRepository.findByUserId(user.getId());
        long resourcesSaved = bookmarkRepository.countByUserId(user.getId());

        long topicsCompleted = userProgressList.stream()
                .filter(UserProgress::isCompleted)
                .count();

        java.util.Set<Long> exploredDomainIds = userProgressList.stream()
                .filter(up -> up.getTopic() != null && up.getTopic().getDomain() != null)
                .map(up -> up.getTopic().getDomain().getId())
                .collect(java.util.stream.Collectors.toSet());
        int domainsExplored = exploredDomainIds.size();

        int averageProgress = 0;
        if (!userProgressList.isEmpty()) {
            int totalPercent = userProgressList.stream()
                    .mapToInt(up -> up.getProgressPercentage() != null ? up.getProgressPercentage() : 0)
                    .sum();
            averageProgress = Math.round((float) totalPercent / userProgressList.size());
        }

        DashboardDataResponse.DashboardStatsDto stats = new DashboardDataResponse.DashboardStatsDto(
                domainsExplored,
                (int) topicsCompleted,
                (int) resourcesSaved,
                averageProgress
        );

        List<DashboardDataResponse.CourseProgressDto> ongoingCourses = userProgressList.stream()
                .filter(up -> up.getTopic() != null)
                .map(up -> {
                    String domainName = (up.getTopic().getDomain() != null) ? up.getTopic().getDomain().getName() : "General";
                    String topicName = up.getTopic().getName();
                    int pct = up.getProgressPercentage() != null ? up.getProgressPercentage() : 0;
                    String desc = up.isCompleted() ? "Completed topic" : ("In progress (" + pct + "%)");
                    String cat = up.isCompleted() ? "Completed" : "In Progress";
                    return new DashboardDataResponse.CourseProgressDto(
                            up.getTopic().getId(),
                            domainName,
                            topicName,
                            topicName,
                            desc,
                            pct,
                            null,
                            cat
                    );
                })
                .collect(java.util.stream.Collectors.toList());

        DashboardDataResponse.CourseProgressDto currentCourse;
        if (!ongoingCourses.isEmpty()) {
            currentCourse = ongoingCourses.get(0);
        } else {
            currentCourse = new DashboardDataResponse.CourseProgressDto(
                    0L,
                    "",
                    "",
                    "No active learning",
                    "Start exploring domains and mark topics as in progress to see them here.",
                    0,
                    null,
                    ""
            );
        }

        return new DashboardDataResponse(stats, currentCourse, ongoingCourses);
    }
}

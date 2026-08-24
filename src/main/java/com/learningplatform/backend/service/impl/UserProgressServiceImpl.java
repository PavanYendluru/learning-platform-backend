package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.dto.UserProgressRequest;
import com.learningplatform.backend.dto.UserProgressResponse;
import com.learningplatform.backend.entity.Topic;
import com.learningplatform.backend.entity.User;
import com.learningplatform.backend.entity.UserProgress;
import com.learningplatform.backend.exception.TopicNotFoundException;
import com.learningplatform.backend.repository.TopicRepository;
import com.learningplatform.backend.repository.UserProgressRepository;
import com.learningplatform.backend.service.CurrentUserService;
import com.learningplatform.backend.service.UserProgressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserProgressServiceImpl implements UserProgressService {

    private final UserProgressRepository progressRepository;
    private final TopicRepository topicRepository;
    private final CurrentUserService currentUserService;

    public UserProgressServiceImpl(UserProgressRepository progressRepository,
                                   TopicRepository topicRepository,
                                   CurrentUserService currentUserService) {
        this.progressRepository = progressRepository;
        this.topicRepository = topicRepository;
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

    private UserProgressResponse mapToResponse(UserProgress p) {
        UserProgressResponse r = new UserProgressResponse();
        r.setId(p.getId());
        r.setTopicId(p.getTopic().getId());
        r.setTopicName(p.getTopic().getName());
        r.setCompleted(p.isCompleted());
        r.setProgressPercentage(p.getProgressPercentage());
        r.setCompletedAt(p.getCompletedAt());
        r.setUpdatedAt(p.getUpdatedAt());
        return r;
    }
}
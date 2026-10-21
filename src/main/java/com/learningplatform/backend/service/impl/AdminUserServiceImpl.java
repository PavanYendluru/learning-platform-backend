package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.dto.AdminUserResponse;
import com.learningplatform.backend.dto.UpdateUserRoleRequest;
import com.learningplatform.backend.dto.UpdateUserStatusRequest;
import com.learningplatform.backend.entity.User;
import com.learningplatform.backend.enums.Role;
import com.learningplatform.backend.exception.ResourceNotFoundException;
import com.learningplatform.backend.repository.BookmarkRepository;
import com.learningplatform.backend.repository.UserProgressRepository;
import com.learningplatform.backend.repository.UserRepository;
import com.learningplatform.backend.service.AdminUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;
    private final BookmarkRepository bookmarkRepository;

    public AdminUserServiceImpl(UserRepository userRepository,
                                UserProgressRepository userProgressRepository,
                                BookmarkRepository bookmarkRepository) {
        this.userRepository = userRepository;
        this.userProgressRepository = userProgressRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllUsers(String roleFilter, String statusFilter) {
        return userRepository.findAll().stream()
                .filter(user -> {
                    if (roleFilter != null && !roleFilter.isBlank() && !"ALL".equalsIgnoreCase(roleFilter)) {
                        if (!user.getRole().name().equalsIgnoreCase(roleFilter)) return false;
                    }
                    if (statusFilter != null && !statusFilter.isBlank() && !"ALL".equalsIgnoreCase(statusFilter)) {
                        String userStatus = user.isEnabled() ? "ACTIVE" : "INACTIVE";
                        if (!userStatus.equalsIgnoreCase(statusFilter)) return false;
                    }
                    return true;
                })
                .map(this::mapToAdminResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToAdminResponse(user);
    }

    @Override
    public AdminUserResponse updateUserRole(Long id, UpdateUserRoleRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        try {
            Role newRole = Role.valueOf(request.getRole().toUpperCase());
            user.setRole(newRole);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role specified: " + request.getRole());
        }

        User saved = userRepository.save(user);
        return mapToAdminResponse(saved);
    }

    @Override
    public AdminUserResponse updateUserStatus(Long id, UpdateUserStatusRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        boolean enabled = "ACTIVE".equalsIgnoreCase(request.getStatus()) || "TRUE".equalsIgnoreCase(request.getStatus());
        user.setEnabled(enabled);

        User saved = userRepository.save(user);
        return mapToAdminResponse(saved);
    }

    private AdminUserResponse mapToAdminResponse(User user) {
        String statusStr = user.isEnabled() ? "ACTIVE" : "INACTIVE";
        AdminUserResponse dto = new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                statusStr,
                user.getCreatedAt()
        );

        int progressCount = (int) userProgressRepository.countByUserId(user.getId());
        int completedCount = (int) userProgressRepository.countByUserIdAndCompletedTrue(user.getId());
        int bookmarksCount = (int) bookmarkRepository.countByUserId(user.getId());

        AdminUserResponse.UserSummary summary = new AdminUserResponse.UserSummary(
                Math.min(progressCount, 5),
                completedCount,
                completedCount * 2,
                bookmarksCount
        );

        dto.setSummary(summary);
        return dto;
    }
}

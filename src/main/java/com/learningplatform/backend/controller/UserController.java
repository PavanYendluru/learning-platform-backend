package com.learningplatform.backend.controller;

import com.learningplatform.backend.dto.ApiResponse;
import com.learningplatform.backend.dto.UpdateProfileRequest;
import com.learningplatform.backend.dto.UserProfileResponse;
import com.learningplatform.backend.entity.User;
import com.learningplatform.backend.repository.UserRepository;
import com.learningplatform.backend.service.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    public UserController(CurrentUserService currentUserService, UserRepository userRepository) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMe() {
        User user = currentUserService.getCurrentUser();
        UserProfileResponse profile = new UserProfileResponse(
                user.getId(), user.getName(), user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
    }

    @PutMapping("/me")
    @Transactional
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMe(
            @Valid @RequestBody UpdateProfileRequest request) {
        User user = currentUserService.getCurrentUser();
        user.setName(request.getName().trim());
        userRepository.save(user);
        UserProfileResponse profile = new UserProfileResponse(
                user.getId(), user.getName(), user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
    }
}

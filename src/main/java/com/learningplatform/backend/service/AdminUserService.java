package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.AdminUserResponse;
import com.learningplatform.backend.dto.UpdateUserRoleRequest;
import com.learningplatform.backend.dto.UpdateUserStatusRequest;

import java.util.List;

public interface AdminUserService {

    List<AdminUserResponse> getAllUsers(String role, String status);

    AdminUserResponse getUserById(Long id);

    AdminUserResponse updateUserRole(Long id, UpdateUserRoleRequest request);

    AdminUserResponse updateUserStatus(Long id, UpdateUserStatusRequest request);
}

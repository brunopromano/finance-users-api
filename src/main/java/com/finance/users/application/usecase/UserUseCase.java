package com.finance.users.application.usecase;

import com.finance.users.application.dto.request.CreateUserRequest;
import com.finance.users.application.dto.request.UpdateUserRequest;
import com.finance.users.application.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserUseCase {

    List<UserResponse> findAll();

    UserResponse findById(UUID id);

    UserResponse create(CreateUserRequest request);

    UserResponse update(UUID id, UpdateUserRequest request);

    void delete(UUID id);
}

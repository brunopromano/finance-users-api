package com.finance.users.application.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(

        String name,

        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {}

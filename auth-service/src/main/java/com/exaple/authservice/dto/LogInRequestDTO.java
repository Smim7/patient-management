package com.exaple.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LogInRequestDTO {
    @NotBlank(message ="Email is required")
    private String email;

    @NotBlank(message ="password is required")
    @Size(min=8,message = "password must be at least 8 characters long")
    private String password;
}

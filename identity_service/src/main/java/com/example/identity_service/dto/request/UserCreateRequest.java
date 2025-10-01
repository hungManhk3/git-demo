package com.example.identity_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 32, message = "USERNAME_VALID_SIZE")
    @Pattern(
            regexp = "^[a-z0-9._-]{3,32}$",
            message = "USERNAME_VALID"
    )
    private String username;

    @Size(min = 8, message = "PASS_VALID_SIZE")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
            message = "PASS_VALID")
    //@JsonProperty("password")
    //private String passwordHash;
    private String password;
    private String fullName;

    @Email(message = "EMAIL_VALID")
    private String email;
    private Short status;
}

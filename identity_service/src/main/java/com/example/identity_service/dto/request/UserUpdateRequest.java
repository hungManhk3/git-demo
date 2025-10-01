package com.example.identity_service.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {

    private String fullName;
    @Email(message = "Invalid email format")
    private String email;
    private Short status;
    private Long version;
}

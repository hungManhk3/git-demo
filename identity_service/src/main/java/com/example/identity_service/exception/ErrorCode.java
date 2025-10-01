package com.example.identity_service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {
    EXCEPTION(9999, "Uncategorized error"),
    INVALID_KEY(999, "Invalid message key"),
    USER_EXITED( 404, "User Exited" ),
    CODE_EXITED( 404, "Code Exited" ),
    FORBIDDEN( 403, "valid token but insufficient permissions" ),
    VERSION_UPDATE(409, "Not Version here"),
    USERNAME_VALID_SIZE(1001, "Username must be between 3 and 32 characters"),
    USERNAME_VALID(1002, "Username can only contain lowercase letters, numbers, dot (.), underscore (_) and dash (-)"),
    PASS_VALID_SIZE(1003, "Password must be at least 8 characters"),
    PASS_VALID(1004, "Password must contain at least one letter and one number"),
    EMAIL_VALID(1005,"Invalid email format"),
    USER_NOT_EXITED( 1006, "User Exited" ),
    UNAUTHENTICATED( 1007, "Unauthenticated" ),
    ROLE_IN_USE( 1008, "Role or permission has been used" ),
    PERMISSION_IN_USE( 1009, "Role or permission has been used" ),
    USER_NOT_FOUND( 1010, "User not found" ),
    PERMISSION_NOT_FOUND( 1011, "Permission not found" ),
    OVERRIDE_NOT_FOUND( 1011, "Override not found" ),
    ;
    private Integer code;
    private String message;
}

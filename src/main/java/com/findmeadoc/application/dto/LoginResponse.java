package com.findmeadoc.application.dto;

public record LoginResponse(
        String email,
        String token,
        Long userID,
        String role
) {
}

package com.findmeadoc.application.dto;

public record LoginRequest(
        String email,
        String password
) {
}

package com.elevance_skills.swiggy_backend_clone.dto;

public record AuthResponse(
        String token,
        String tokenType,
        String id,
        String name,
        String email,
        String role
) {
}
package com.elevance_skills.swiggy_backend_clone.dto;

public class RestrictUserRequest {
    private int durationMinutes;
    private String reason;
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

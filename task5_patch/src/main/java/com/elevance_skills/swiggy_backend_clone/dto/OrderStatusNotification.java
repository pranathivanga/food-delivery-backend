package com.elevance_skills.swiggy_backend_clone.dto;

import java.time.Instant;

public class OrderStatusNotification {
    private String orderId;
    private String userEmail;
    private String status;
    private String note;
    private Instant changedAt;

    public OrderStatusNotification(String orderId, String userEmail, String status, String note, Instant changedAt) {
        this.orderId = orderId;
        this.userEmail = userEmail;
        this.status = status;
        this.note = note;
        this.changedAt = changedAt;
    }
    public String getOrderId() { return orderId; }
    public String getUserEmail() { return userEmail; }
    public String getStatus() { return status; }
    public String getNote() { return note; }
    public Instant getChangedAt() { return changedAt; }
}

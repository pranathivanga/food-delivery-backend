package com.elevance_skills.swiggy_backend_clone.model;

import java.time.Instant;

public class OrderStatusEvent {
    private String status;
    private String note;
    private Instant changedAt;
    private String changedBy;

    public OrderStatusEvent() {}
    public OrderStatusEvent(String status, String note, Instant changedAt, String changedBy) {
        this.status = status;
        this.note = note;
        this.changedAt = changedAt;
        this.changedBy = changedBy;
    }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public Instant getChangedAt() { return changedAt; }
    public void setChangedAt(Instant changedAt) { this.changedAt = changedAt; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
}

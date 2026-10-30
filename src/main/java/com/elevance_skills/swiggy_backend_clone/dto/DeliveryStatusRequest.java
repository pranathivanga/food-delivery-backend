package com.elevance_skills.swiggy_backend_clone.dto;

public class DeliveryStatusRequest {
    private String partnerEmail;
    private boolean available;
    public String getPartnerEmail() { return partnerEmail; }
    public void setPartnerEmail(String partnerEmail) { this.partnerEmail = partnerEmail; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}


package com.elevance_skills.swiggy_backend_clone.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private String userEmail;
    private String restaurantId;
    private List<OrderItem> items = new ArrayList<>();

    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal deliveryFee = BigDecimal.ZERO;
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private String status = "PLACED";
    private Instant createdAt;
    private Instant updatedAt;
    private Instant cancelledAt;

    private String couponCode;
    private int riskScore;
    private boolean fraudFlagged;
    private List<String> fraudReasons = new ArrayList<>();
    private String fraudReviewStatus = "NOT_REQUIRED";

    private String assignedPartnerId;
    private String assignedPartnerName;
    private String deliveryAssignmentStatus = "UNASSIGNED";
    private List<OrderStatusEvent> statusHistory = new ArrayList<>();

    public Order() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }
    public boolean isFraudFlagged() { return fraudFlagged; }
    public void setFraudFlagged(boolean fraudFlagged) { this.fraudFlagged = fraudFlagged; }
    public List<String> getFraudReasons() { return fraudReasons; }
    public void setFraudReasons(List<String> fraudReasons) { this.fraudReasons = fraudReasons; }
    public String getFraudReviewStatus() { return fraudReviewStatus; }
    public void setFraudReviewStatus(String fraudReviewStatus) { this.fraudReviewStatus = fraudReviewStatus; }

    public String getAssignedPartnerId() { return assignedPartnerId; }
    public void setAssignedPartnerId(String assignedPartnerId) { this.assignedPartnerId = assignedPartnerId; }
    public String getAssignedPartnerName() { return assignedPartnerName; }
    public void setAssignedPartnerName(String assignedPartnerName) { this.assignedPartnerName = assignedPartnerName; }
    public String getDeliveryAssignmentStatus() { return deliveryAssignmentStatus; }
    public void setDeliveryAssignmentStatus(String deliveryAssignmentStatus) { this.deliveryAssignmentStatus = deliveryAssignmentStatus; }
    public List<OrderStatusEvent> getStatusHistory() { return statusHistory; }
    public void setStatusHistory(List<OrderStatusEvent> statusHistory) { this.statusHistory = statusHistory; }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}

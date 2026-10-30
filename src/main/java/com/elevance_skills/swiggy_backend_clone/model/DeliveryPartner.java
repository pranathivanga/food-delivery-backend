package com.elevance_skills.swiggy_backend_clone.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "delivery_partners")
public class DeliveryPartner {
    @Id private String id;
    private String name;
    private String email;
    private String phone;
    private double latitude;
    private double longitude;
    private boolean available = true;
    private boolean active = true;
    private int currentWorkload = 0;
    private int maxWorkload = 3;
    private List<String> assignedOrderIds = new ArrayList<>();

    public DeliveryPartner() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public int getCurrentWorkload() { return currentWorkload; }
    public void setCurrentWorkload(int currentWorkload) { this.currentWorkload = currentWorkload; }
    public int getMaxWorkload() { return maxWorkload; }
    public void setMaxWorkload(int maxWorkload) { this.maxWorkload = maxWorkload; }
    public List<String> getAssignedOrderIds() { return assignedOrderIds; }
    public void setAssignedOrderIds(List<String> assignedOrderIds) { this.assignedOrderIds = assignedOrderIds; }
}

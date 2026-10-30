package com.elevance_skills.swiggy_backend_clone.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "restaurants")
public class Restaurant {
    @Id
    private String id;
    @Indexed
    private String name;
    @Indexed
    private String cuisine;
    private double rating;
    private double priceForTwo;
    private int deliveryTime;
    private boolean vegetarian;
    private long popularity;
    private String description;
    private boolean active = true;
    private List<String> tags = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public double getPriceForTwo() { return priceForTwo; }
    public void setPriceForTwo(double priceForTwo) { this.priceForTwo = priceForTwo; }
    public int getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(int deliveryTime) { this.deliveryTime = deliveryTime; }
    public boolean isVegetarian() { return vegetarian; }
    public void setVegetarian(boolean vegetarian) { this.vegetarian = vegetarian; }
    public long getPopularity() { return popularity; }
    public void setPopularity(long popularity) { this.popularity = popularity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}

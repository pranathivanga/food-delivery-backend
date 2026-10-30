package com.elevance_skills.swiggy_backend_clone.repository;

import com.elevance_skills.swiggy_backend_clone.model.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
}

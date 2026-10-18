
package com.elevance_skills.swiggy_backend_clone.repository;

import com.elevance_skills.swiggy_backend_clone.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository
        extends MongoRepository<Order, String> {

    List<Order> findByUserEmailOrderByCreatedAtDesc(
            String userEmail
    );

    Optional<Order> findByIdAndUserEmail(
            String id,
            String userEmail
    );
}

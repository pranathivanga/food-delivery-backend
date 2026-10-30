package com.elevance_skills.swiggy_backend_clone.controller;

import com.elevance_skills.swiggy_backend_clone.dto.RestrictUserRequest;
import com.elevance_skills.swiggy_backend_clone.model.Order;
import com.elevance_skills.swiggy_backend_clone.model.User;
import com.elevance_skills.swiggy_backend_clone.repository.OrderRepository;
import com.elevance_skills.swiggy_backend_clone.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/fraud")
public class FraudAdminController {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public FraudAdminController(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/orders")
    public List<Order> getFlaggedOrders() {
        return orderRepository.findByFraudFlaggedTrueOrderByCreatedAtDesc().stream()
                .filter(o -> "PENDING".equals(o.getFraudReviewStatus())).toList();
    }

    @PutMapping("/orders/{orderId}/approve")
    public Order approve(@PathVariable String orderId) {
        Order order = findOrder(orderId);
        if (!"PENDING".equals(order.getFraudReviewStatus()))
            throw new IllegalStateException("Order is not awaiting fraud review");
        order.setFraudReviewStatus("APPROVED");
        order.setFraudFlagged(false);
        order.setStatus("PLACED");
        order.setUpdatedAt(Instant.now());
        return orderRepository.save(order);
    }

    @PutMapping("/orders/{orderId}/reject")
    public Order reject(@PathVariable String orderId) {
        Order order = findOrder(orderId);
        if (!"PENDING".equals(order.getFraudReviewStatus()))
            throw new IllegalStateException("Order is not awaiting fraud review");
        order.setFraudReviewStatus("REJECTED");
        order.setFraudFlagged(true);
        order.setStatus("REJECTED");
        order.setUpdatedAt(Instant.now());
        return orderRepository.save(order);
    }

    @PutMapping("/users/{email}/restrict")
    public ResponseEntity<?> restrictUser(@PathVariable String email,
                                          @RequestBody RestrictUserRequest request) {
        if (request.getDurationMinutes() <= 0)
            return ResponseEntity.badRequest().body(Map.of("error", "durationMinutes must be greater than zero"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        user.setRestricted(true);
        user.setRestrictedUntil(Instant.now().plus(Duration.ofMinutes(request.getDurationMinutes())));
        user.setRestrictionReason(request.getReason());
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User temporarily restricted",
                "email", user.getEmail(), "restrictedUntil", user.getRestrictedUntil().toString()));
    }

    private Order findOrder(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Order not found"));
    }
}

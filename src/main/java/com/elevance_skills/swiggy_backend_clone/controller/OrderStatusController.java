package com.elevance_skills.swiggy_backend_clone.controller;

import com.elevance_skills.swiggy_backend_clone.dto.OrderStatusUpdateRequest;
import com.elevance_skills.swiggy_backend_clone.model.Order;
import com.elevance_skills.swiggy_backend_clone.service.OrderTrackingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderStatusController {
    private final OrderTrackingService trackingService;
    public OrderStatusController(OrderTrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @PutMapping("/update-status/{orderId}")
    public ResponseEntity<?> updateStatus(@PathVariable String orderId,
                                          @Valid @RequestBody OrderStatusUpdateRequest request,
                                          Authentication authentication) {
        boolean admin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ADMIN".equals(a.getAuthority()));
        if (!admin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "ADMIN role is required to update order status"));
        }
        Order updated = trackingService.updateStatus(orderId, request.getStatus(), request.getNote(), authentication.getName());
        return ResponseEntity.ok(updated);
    }
}

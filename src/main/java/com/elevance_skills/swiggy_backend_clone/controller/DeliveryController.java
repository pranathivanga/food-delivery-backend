package com.elevance_skills.swiggy_backend_clone.controller;

import com.elevance_skills.swiggy_backend_clone.dto.DeliveryStatusRequest;
import com.elevance_skills.swiggy_backend_clone.dto.DeclineDeliveryRequest;
import com.elevance_skills.swiggy_backend_clone.model.DeliveryPartner;
import com.elevance_skills.swiggy_backend_clone.model.Order;
import com.elevance_skills.swiggy_backend_clone.service.DeliveryAssignmentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {
    private final DeliveryAssignmentService assignmentService;
    public DeliveryController(DeliveryAssignmentService assignmentService) { this.assignmentService = assignmentService; }

    @PutMapping("/set-status")
    public DeliveryPartner setStatus(@RequestBody DeliveryStatusRequest request) {
        return assignmentService.setAvailability(request.getPartnerEmail(), request.isAvailable());
    }

    @PostMapping("/orders/{orderId}/assign")
    public Order assign(@PathVariable String orderId, @RequestParam double restaurantLatitude,
                        @RequestParam double restaurantLongitude) {
        return assignmentService.assign(orderId, restaurantLatitude, restaurantLongitude);
    }

    @PostMapping("/orders/{orderId}/decline")
    public Order decline(@PathVariable String orderId, @RequestBody DeclineDeliveryRequest request) {
        return assignmentService.declineAndReassign(orderId, request.getPartnerEmail(),
                request.getRestaurantLatitude(), request.getRestaurantLongitude());
    }
}

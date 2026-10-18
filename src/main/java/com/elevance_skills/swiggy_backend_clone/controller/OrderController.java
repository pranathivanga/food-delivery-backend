
package com.elevance_skills.swiggy_backend_clone.controller;

import com.elevance_skills.swiggy_backend_clone.dto.CreateOrderRequest;
import com.elevance_skills.swiggy_backend_clone.model.Order;
import com.elevance_skills.swiggy_backend_clone.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {

        return orderService.createOrder(
                request,
                authentication.getName()
        );
    }

    @GetMapping
    public List<Order> getMyOrders(
            Authentication authentication) {

        return orderService.getMyOrders(
                authentication.getName()
        );
    }

    @GetMapping("/{orderId}")
    public Order getOrder(
            @PathVariable String orderId,
            Authentication authentication) {

        return orderService.getMyOrder(
                orderId,
                authentication.getName()
        );
    }

    @PostMapping("/cancel/{orderId}")
    public Order cancelOrder(
            @PathVariable String orderId,
            Authentication authentication) {

        return orderService.cancelOrder(
                orderId,
                authentication.getName()
        );
    }
}

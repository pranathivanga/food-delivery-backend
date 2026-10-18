
package com.elevance_skills.swiggy_backend_clone.service;

import com.elevance_skills.swiggy_backend_clone.dto.CreateOrderRequest;
import com.elevance_skills.swiggy_backend_clone.model.Order;
import com.elevance_skills.swiggy_backend_clone.model.OrderItem;
import com.elevance_skills.swiggy_backend_clone.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(
            CreateOrderRequest request,
            String userEmail) {

        Order order = new Order();
        order.setUserEmail(userEmail);
        order.setRestaurantId(request.getRestaurantId());

        List<OrderItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CreateOrderRequest.OrderItemRequest requested
                : request.getItems()) {

            BigDecimal lineTotal = requested.getUnitPrice()
                    .multiply(BigDecimal.valueOf(
                            requested.getQuantity()
                    ));

            subtotal = subtotal.add(lineTotal);

            items.add(new OrderItem(
                    requested.getItemId(),
                    requested.getItemName(),
                    requested.getQuantity(),
                    requested.getUnitPrice()
            ));
        }

        Instant now = Instant.now();

        order.setItems(items);
        order.setSubtotal(subtotal);

        // Surge pricing will replace this default in a later task.
        order.setDeliveryFee(BigDecimal.ZERO);
        order.setTotalAmount(subtotal);
        order.setStatus("PLACED");
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        return orderRepository.save(order);
    }

    public List<Order> getMyOrders(String userEmail) {
        return orderRepository
                .findByUserEmailOrderByCreatedAtDesc(userEmail);
    }

    public Order getMyOrder(
            String orderId,
            String userEmail) {

        return orderRepository
                .findByIdAndUserEmail(orderId, userEmail)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        )
                );
    }

    public Order cancelOrder(
            String orderId,
            String userEmail) {

        Order order = getMyOrder(orderId, userEmail);

        if (!"PLACED".equals(order.getStatus())) {
            throw new IllegalStateException(
                    "Only orders with PLACED status can be cancelled"
            );
        }

        Instant now = Instant.now();

        order.setStatus("CANCELLED");
        order.setCancelledAt(now);
        order.setUpdatedAt(now);

        return orderRepository.save(order);
    }
}

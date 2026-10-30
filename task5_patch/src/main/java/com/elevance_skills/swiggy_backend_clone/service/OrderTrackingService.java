package com.elevance_skills.swiggy_backend_clone.service;

import com.elevance_skills.swiggy_backend_clone.dto.OrderStatusNotification;
import com.elevance_skills.swiggy_backend_clone.model.Order;
import com.elevance_skills.swiggy_backend_clone.model.OrderStatusEvent;
import com.elevance_skills.swiggy_backend_clone.repository.OrderRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class OrderTrackingService {
    private static final Map<String, Set<String>> ALLOWED_NEXT = Map.of(
            "PLACED", Set.of("ACCEPTED", "CANCELLED"),
            "ACCEPTED", Set.of("PREPARING", "CANCELLED"),
            "PREPARING", Set.of("OUT_FOR_DELIVERY"),
            "OUT_FOR_DELIVERY", Set.of("DELIVERED")
    );

    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public OrderTrackingService(OrderRepository orderRepository, SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public Order updateStatus(String orderId, String requestedStatus, String note, String changedBy) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found"));
        String current = order.getStatus() == null ? "" : order.getStatus().toUpperCase(Locale.ROOT);
        String next = requestedStatus == null ? "" : requestedStatus.trim().toUpperCase(Locale.ROOT);

        if ("PENDING_FRAUD_REVIEW".equals(current)) {
            throw new IllegalStateException("Order must be approved by fraud review before status updates");
        }
        if (!ALLOWED_NEXT.getOrDefault(current, Set.of()).contains(next)) {
            throw new IllegalStateException("Invalid order status transition: " + current + " -> " + next);
        }

        Instant now = Instant.now();
        order.setStatus(next);
        order.setUpdatedAt(now);
        if (order.getStatusHistory() == null) order.setStatusHistory(new ArrayList<>());
        order.getStatusHistory().add(new OrderStatusEvent(next, note, now, changedBy));
        Order saved = orderRepository.save(order);

        OrderStatusNotification notification = new OrderStatusNotification(
                saved.getId(), saved.getUserEmail(), saved.getStatus(), note, now);
        // Order-specific topic is convenient for Postman/WebSocket demonstrations.
        messagingTemplate.convertAndSend("/topic/orders/" + saved.getId(), notification);
        // User queue is the customer-facing destination when the WebSocket handshake is authenticated.
        if (saved.getUserEmail() != null && !saved.getUserEmail().isBlank()) {
            messagingTemplate.convertAndSendToUser(saved.getUserEmail(), "/queue/orders", notification);
        }
        return saved;
    }
}

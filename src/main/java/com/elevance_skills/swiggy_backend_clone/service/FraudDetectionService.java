package com.elevance_skills.swiggy_backend_clone.service;

import com.elevance_skills.swiggy_backend_clone.model.Order;
import com.elevance_skills.swiggy_backend_clone.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class FraudDetectionService {

    private final OrderRepository orderRepository;

    public FraudDetectionService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Assessment assess(String email, String couponCode, boolean newCancellation) {
        List<Order> history = orderRepository.findByUserEmailOrderByCreatedAtDesc(email);
        Instant now = Instant.now();
        Instant tenMinutesAgo = now.minus(Duration.ofMinutes(10));
        Instant oneDayAgo = now.minus(Duration.ofHours(24));
        int score = 0;
        List<String> reasons = new ArrayList<>();

        long recentOrders = history.stream()
                .filter(o -> o.getCreatedAt() != null && !o.getCreatedAt().isBefore(tenMinutesAgo))
                .count();
        if (recentOrders >= 2) {
            score += 25;
            reasons.add("Multiple orders placed within 10 minutes");
        }

        long recentCancellations = history.stream()
                .filter(o -> "CANCELLED".equalsIgnoreCase(o.getStatus()))
                .filter(o -> o.getCancelledAt() != null && !o.getCancelledAt().isBefore(oneDayAgo))
                .count();
        if (newCancellation) recentCancellations++;
        if (recentCancellations >= 3) {
            score += 40;
            reasons.add("Repeated order cancellations within 24 hours");
        }

        if (couponCode != null && !couponCode.isBlank()) {
            String normalized = couponCode.trim().toLowerCase(Locale.ROOT);
            long uses = history.stream()
                    .filter(o -> o.getCouponCode() != null)
                    .filter(o -> normalized.equals(o.getCouponCode().trim().toLowerCase(Locale.ROOT)))
                    .count();
            if (uses >= 2) {
                score += 25;
                reasons.add("Repeated use of the same coupon");
            }
        }

        long refunds = history.stream()
                .filter(o -> "REFUNDED".equalsIgnoreCase(o.getStatus()))
                .count();
        if (refunds >= 3) {
            score += 40;
            reasons.add("High number of previous refunds");
        }

        return new Assessment(score, score >= 40, reasons);
    }

    public record Assessment(int score, boolean flagged, List<String> reasons) {}
}

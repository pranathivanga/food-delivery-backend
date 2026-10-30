package com.elevance_skills.swiggy_backend_clone.service;

import com.elevance_skills.swiggy_backend_clone.model.DeliveryPartner;
import com.elevance_skills.swiggy_backend_clone.model.Order;
import com.elevance_skills.swiggy_backend_clone.repository.DeliveryPartnerRepository;
import com.elevance_skills.swiggy_backend_clone.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class DeliveryAssignmentService {
    private final DeliveryPartnerRepository partnerRepository;
    private final OrderRepository orderRepository;

    public DeliveryAssignmentService(DeliveryPartnerRepository partnerRepository, OrderRepository orderRepository) {
        this.partnerRepository = partnerRepository;
        this.orderRepository = orderRepository;
    }

    public Order assign(String orderId, double restaurantLat, double restaurantLon) {
        return assignExcluding(orderId, restaurantLat, restaurantLon, null);
    }

    private Order assignExcluding(String orderId, double restaurantLat, double restaurantLon, String excludedEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found"));
        List<DeliveryPartner> candidates = partnerRepository.findByActiveTrueAndAvailableTrue();
        DeliveryPartner selected = candidates.stream()
                .filter(p -> p.getCurrentWorkload() < p.getMaxWorkload())
                .filter(p -> excludedEmail == null || !excludedEmail.equalsIgnoreCase(p.getEmail()))
                .min(Comparator.comparingInt(DeliveryPartner::getCurrentWorkload)
                        .thenComparingDouble(p -> distanceKm(restaurantLat, restaurantLon, p.getLatitude(), p.getLongitude())))
                .orElseThrow(() -> new IllegalStateException("No other available delivery partner"));

        selected.getAssignedOrderIds().add(orderId);
        selected.setCurrentWorkload(selected.getCurrentWorkload() + 1);
        if (selected.getCurrentWorkload() >= selected.getMaxWorkload()) selected.setAvailable(false);
        partnerRepository.save(selected);

        order.setAssignedPartnerId(selected.getId());
        order.setAssignedPartnerName(selected.getName());
        order.setDeliveryAssignmentStatus("ASSIGNED");
        order.setUpdatedAt(Instant.now());
        return orderRepository.save(order);
    }

    public Order declineAndReassign(String orderId, String partnerEmail, double restaurantLat, double restaurantLon) {
        DeliveryPartner declining = partnerRepository.findByEmail(partnerEmail)
                .orElseThrow(() -> new IllegalStateException("Delivery partner not found"));
        if (declining.getAssignedOrderIds().remove(orderId)) {
            declining.setCurrentWorkload(Math.max(0, declining.getCurrentWorkload() - 1));
            declining.setAvailable(declining.isActive() && declining.getCurrentWorkload() < declining.getMaxWorkload());
            partnerRepository.save(declining);
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found"));
        order.setAssignedPartnerId(null);
        order.setAssignedPartnerName(null);
        order.setDeliveryAssignmentStatus("REASSIGNMENT_REQUIRED");
        orderRepository.save(order);
        return assignExcluding(orderId, restaurantLat, restaurantLon, partnerEmail);
    }

    public DeliveryPartner setAvailability(String email, boolean available) {
        DeliveryPartner partner = partnerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Delivery partner not found"));
        if (available && (!partner.isActive() || partner.getCurrentWorkload() >= partner.getMaxWorkload())) {
            throw new IllegalStateException("Partner cannot become available while inactive or at maximum workload");
        }
        partner.setAvailable(available);
        return partnerRepository.save(partner);
    }

    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * earthRadius * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}

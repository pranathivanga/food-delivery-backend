package com.elevance_skills.swiggy_backend_clone.service;

import com.elevance_skills.swiggy_backend_clone.dto.CreateOrderRequest;
import com.elevance_skills.swiggy_backend_clone.model.Order;
import com.elevance_skills.swiggy_backend_clone.model.OrderItem;
import com.elevance_skills.swiggy_backend_clone.model.User;
import com.elevance_skills.swiggy_backend_clone.repository.OrderRepository;
import com.elevance_skills.swiggy_backend_clone.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final FraudDetectionService fraudDetectionService;
    private final SurgePricingService surgePricingService;
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, FraudDetectionService fraudDetectionService, SurgePricingService surgePricingService){this.orderRepository=orderRepository;this.userRepository=userRepository;this.fraudDetectionService=fraudDetectionService;this.surgePricingService=surgePricingService;}
    public Order createOrder(CreateOrderRequest request, String userEmail){
        checkUserRestriction(userEmail); Order order=new Order(); order.setUserEmail(userEmail); order.setRestaurantId(request.getRestaurantId());
        List<OrderItem> items=new ArrayList<>(); BigDecimal subtotal=BigDecimal.ZERO;
        for(CreateOrderRequest.OrderItemRequest requested:request.getItems()){
            BigDecimal lineTotal=requested.getUnitPrice().multiply(BigDecimal.valueOf(requested.getQuantity())); subtotal=subtotal.add(lineTotal);
            items.add(new OrderItem(requested.getItemId(),requested.getItemName(),requested.getQuantity(),requested.getUnitPrice()));
        }
        Instant now=Instant.now(); order.setItems(items); order.setSubtotal(subtotal);
        BigDecimal deliveryFee=surgePricingService.calculateFee(request.getRestaurantId());
        order.setDeliveryFee(deliveryFee); order.setTotalAmount(subtotal.add(deliveryFee)); order.setCreatedAt(now); order.setUpdatedAt(now); order.setCouponCode(request.getCouponCode());
        FraudDetectionService.Assessment assessment=fraudDetectionService.assess(userEmail,request.getCouponCode(),false);
        order.setRiskScore(assessment.score()); order.setFraudFlagged(assessment.flagged()); order.setFraudReasons(assessment.reasons());
        order.setFraudReviewStatus(assessment.flagged()?"PENDING":"NOT_REQUIRED"); order.setStatus(assessment.flagged()?"PENDING_FRAUD_REVIEW":"PLACED");
        return orderRepository.save(order);
    }
    public List<Order> getMyOrders(String userEmail){return orderRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);}
    public Order getMyOrder(String orderId,String userEmail){return orderRepository.findByIdAndUserEmail(orderId,userEmail).orElseThrow(()->new RuntimeException("Order not found"));}
    public Order cancelOrder(String orderId,String userEmail){
        checkUserRestriction(userEmail); Order order=getMyOrder(orderId,userEmail);
        if(!"PLACED".equals(order.getStatus())) throw new IllegalStateException("Only orders with PLACED status can be cancelled");
        FraudDetectionService.Assessment assessment=fraudDetectionService.assess(userEmail,null,true);
        if(assessment.flagged()){order.setRiskScore(Math.max(order.getRiskScore(),assessment.score()));order.setFraudFlagged(true);order.setFraudReasons(assessment.reasons());order.setFraudReviewStatus("PENDING");order.setStatus("PENDING_FRAUD_REVIEW");order.setUpdatedAt(Instant.now());return orderRepository.save(order);}
        Instant now=Instant.now();order.setStatus("CANCELLED");order.setCancelledAt(now);order.setUpdatedAt(now);return orderRepository.save(order);
    }
    private void checkUserRestriction(String email){User user=userRepository.findByEmail(email).orElseThrow(()->new IllegalStateException("User account not found"));if(!user.isRestricted())return;Instant until=user.getRestrictedUntil();if(until==null||Instant.now().isBefore(until))throw new IllegalStateException("Your account is temporarily restricted");user.setRestricted(false);user.setRestrictedUntil(null);user.setRestrictionReason(null);userRepository.save(user);}
}

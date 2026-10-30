package com.elevance_skills.swiggy_backend_clone.controller;

import com.elevance_skills.swiggy_backend_clone.dto.DeliveryFeeRequest;
import com.elevance_skills.swiggy_backend_clone.model.SurgeSettings;
import com.elevance_skills.swiggy_backend_clone.service.SurgePricingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

@RestController
public class SurgePricingController {
    private final SurgePricingService service;
    public SurgePricingController(SurgePricingService service){this.service=service;}
    @PostMapping("/api/orders/calculate-delivery-fee")
    public ResponseEntity<?> calculate(@RequestBody DeliveryFeeRequest request){BigDecimal fee=service.calculateFee(request.getRestaurantId());return ResponseEntity.ok(Map.of("restaurantId",request.getRestaurantId()==null?"":request.getRestaurantId(),"deliveryFee",fee));}
    @GetMapping("/api/admin/surge-settings")
    public SurgeSettings getSettings(){return service.getSettings();}
    @PutMapping("/api/admin/surge-settings")
    public SurgeSettings updateSettings(@RequestBody SurgeSettings settings){return service.updateSettings(settings);}
}

package com.elevance_skills.swiggy_backend_clone.controller;

import com.elevance_skills.swiggy_backend_clone.dto.CreateDeliveryPartnerRequest;
import com.elevance_skills.swiggy_backend_clone.model.DeliveryPartner;
import com.elevance_skills.swiggy_backend_clone.repository.DeliveryPartnerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/delivery-partners")
public class DeliveryAdminController {
    private final DeliveryPartnerRepository repository;
    public DeliveryAdminController(DeliveryPartnerRepository repository) { this.repository = repository; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryPartner create(@RequestBody CreateDeliveryPartnerRequest request) {
        if (request.getName() == null || request.getEmail() == null || request.getMaxWorkload() < 1)
            throw new IllegalArgumentException("Name, email and positive maxWorkload are required");
        if (repository.findByEmail(request.getEmail()).isPresent())
            throw new IllegalStateException("Delivery partner email already exists");
        DeliveryPartner p = new DeliveryPartner();
        p.setName(request.getName()); p.setEmail(request.getEmail()); p.setPhone(request.getPhone());
        p.setLatitude(request.getLatitude()); p.setLongitude(request.getLongitude());
        p.setMaxWorkload(request.getMaxWorkload());
        return repository.save(p);
    }

    @GetMapping
    public List<DeliveryPartner> list() { return repository.findAll(); }
}

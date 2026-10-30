package com.elevance_skills.swiggy_backend_clone.repository;

import com.elevance_skills.swiggy_backend_clone.model.DeliveryPartner;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface DeliveryPartnerRepository extends MongoRepository<DeliveryPartner, String> {
    List<DeliveryPartner> findByActiveTrueAndAvailableTrue();
    Optional<DeliveryPartner> findByEmail(String email);
}

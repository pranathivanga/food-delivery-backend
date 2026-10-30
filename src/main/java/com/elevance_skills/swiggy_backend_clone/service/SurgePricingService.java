package com.elevance_skills.swiggy_backend_clone.service;

import com.elevance_skills.swiggy_backend_clone.model.Order;
import com.elevance_skills.swiggy_backend_clone.model.SurgeSettings;
import com.elevance_skills.swiggy_backend_clone.repository.OrderRepository;
import com.elevance_skills.swiggy_backend_clone.repository.SurgeSettingsRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalTime;

@Service
public class SurgePricingService {
    private final SurgeSettingsRepository settingsRepository;
    private final OrderRepository orderRepository;
    public SurgePricingService(SurgeSettingsRepository settingsRepository, OrderRepository orderRepository){this.settingsRepository=settingsRepository;this.orderRepository=orderRepository;}
    public SurgeSettings getSettings(){return settingsRepository.findById("default").orElseGet(()->settingsRepository.save(new SurgeSettings()));}
    public SurgeSettings updateSettings(SurgeSettings settings){settings.setId("default"); validate(settings); return settingsRepository.save(settings);}
    public BigDecimal calculateFee(String restaurantId){
        SurgeSettings s=getSettings(); LocalTime now=LocalTime.now();
        boolean peak=between(now,s.getPeakStart(),s.getPeakEnd()) || between(now,s.getDinnerStart(),s.getDinnerEnd());
        Instant since=Instant.now().minusSeconds(Math.max(1,s.getDemandWindowMinutes())*60L);
        long recent=orderRepository.findAll().stream().filter(o->o.getCreatedAt()!=null && !o.getCreatedAt().isBefore(since)).filter(o->restaurantId==null || restaurantId.isBlank() || restaurantId.equals(o.getRestaurantId())).count();
        BigDecimal multiplier=BigDecimal.ONE;
        if(peak) multiplier=multiplier.multiply(s.getPeakMultiplier());
        if(recent>=s.getDemandOrderThreshold()) multiplier=multiplier.multiply(s.getHighDemandMultiplier());
        return s.getBaseDeliveryFee().multiply(multiplier).setScale(2,RoundingMode.HALF_UP);
    }
    private boolean between(LocalTime t,LocalTime a,LocalTime b){return a!=null&&b!=null&&!t.isBefore(a)&&t.isBefore(b);}
    private void validate(SurgeSettings s){if(s.getBaseDeliveryFee()==null||s.getBaseDeliveryFee().signum()<0||s.getPeakMultiplier()==null||s.getPeakMultiplier().compareTo(BigDecimal.ONE)<0||s.getHighDemandMultiplier()==null||s.getHighDemandMultiplier().compareTo(BigDecimal.ONE)<0||s.getDemandOrderThreshold()<1||s.getDemandWindowMinutes()<1) throw new IllegalArgumentException("Invalid surge pricing settings");}
}

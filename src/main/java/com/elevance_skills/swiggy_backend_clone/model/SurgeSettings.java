package com.elevance_skills.swiggy_backend_clone.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalTime;

@Document(collection = "surge_settings")
public class SurgeSettings {
    @Id private String id = "default";
    private BigDecimal baseDeliveryFee = new BigDecimal("30.00");
    private BigDecimal peakMultiplier = new BigDecimal("1.5");
    private BigDecimal highDemandMultiplier = new BigDecimal("1.3");
    private int demandOrderThreshold = 5;
    private int demandWindowMinutes = 30;
    private LocalTime peakStart = LocalTime.of(12, 0);
    private LocalTime peakEnd = LocalTime.of(15, 0);
    private LocalTime dinnerStart = LocalTime.of(19, 0);
    private LocalTime dinnerEnd = LocalTime.of(22, 0);
    public String getId(){return id;} public void setId(String v){id=v;}
    public BigDecimal getBaseDeliveryFee(){return baseDeliveryFee;} public void setBaseDeliveryFee(BigDecimal v){baseDeliveryFee=v;}
    public BigDecimal getPeakMultiplier(){return peakMultiplier;} public void setPeakMultiplier(BigDecimal v){peakMultiplier=v;}
    public BigDecimal getHighDemandMultiplier(){return highDemandMultiplier;} public void setHighDemandMultiplier(BigDecimal v){highDemandMultiplier=v;}
    public int getDemandOrderThreshold(){return demandOrderThreshold;} public void setDemandOrderThreshold(int v){demandOrderThreshold=v;}
    public int getDemandWindowMinutes(){return demandWindowMinutes;} public void setDemandWindowMinutes(int v){demandWindowMinutes=v;}
    public LocalTime getPeakStart(){return peakStart;} public void setPeakStart(LocalTime v){peakStart=v;}
    public LocalTime getPeakEnd(){return peakEnd;} public void setPeakEnd(LocalTime v){peakEnd=v;}
    public LocalTime getDinnerStart(){return dinnerStart;} public void setDinnerStart(LocalTime v){dinnerStart=v;}
    public LocalTime getDinnerEnd(){return dinnerEnd;} public void setDinnerEnd(LocalTime v){dinnerEnd=v;}
}

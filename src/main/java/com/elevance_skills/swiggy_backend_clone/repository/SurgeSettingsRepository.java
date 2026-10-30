package com.elevance_skills.swiggy_backend_clone.repository;
import com.elevance_skills.swiggy_backend_clone.model.SurgeSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface SurgeSettingsRepository extends MongoRepository<SurgeSettings, String> {}

package com.elevance_skills.swiggy_backend_clone.controller;

import com.elevance_skills.swiggy_backend_clone.repository.UserRepository;
import com.elevance_skills.swiggy_backend_clone.service.RecommendationService;
import org.bson.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/recommendations")
public class RecommendationAdminController {
    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    public RecommendationAdminController(RecommendationService recommendationService,
                                         UserRepository userRepository) {
        this.recommendationService = recommendationService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}")
    public List<Document> recommendationsForUser(@PathVariable String userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return recommendationService.recommend(user.getEmail());
    }
}

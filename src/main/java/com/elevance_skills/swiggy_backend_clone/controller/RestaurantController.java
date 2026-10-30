package com.elevance_skills.swiggy_backend_clone.controller;

import com.elevance_skills.swiggy_backend_clone.model.Restaurant;
import com.elevance_skills.swiggy_backend_clone.service.RestaurantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private final RestaurantService service;
    public RestaurantController(RestaurantService service) { this.service = service; }

    @GetMapping("/search")
    public List<Restaurant> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer maxDeliveryTime,
            @RequestParam(required = false) Boolean vegetarian,
            @RequestParam(required = false, defaultValue = "popularity") String sortBy) {
        return service.search(q, cuisine, rating, minPrice, maxPrice, maxDeliveryTime, vegetarian, sortBy);
    }
}

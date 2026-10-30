package com.elevance_skills.swiggy_backend_clone.controller;

import com.elevance_skills.swiggy_backend_clone.model.Restaurant;
import com.elevance_skills.swiggy_backend_clone.service.RestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/restaurants")
public class AdminRestaurantController {
    private final RestaurantService service;
    public AdminRestaurantController(RestaurantService service) { this.service = service; }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant create(@RequestBody Restaurant restaurant) {
        return service.create(restaurant);
    }

    @PutMapping("/update/{restaurantId}")
    public Restaurant update(@PathVariable String restaurantId, @RequestBody Restaurant restaurant) {
        return service.update(restaurantId, restaurant);
    }
}

package com.elevance_skills.swiggy_backend_clone.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminTestController {

    @GetMapping("/test")
    public Map<String, String> testAdminAccess() {
        return Map.of(
                "message", "Admin access granted",
                "status", "SUCCESS"
        );
    }
}
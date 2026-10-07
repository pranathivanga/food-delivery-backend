package com.elevance_skills.swiggy_backend_clone.controller;

import com.elevance_skills.swiggy_backend_clone.dto.UserRequest;
import com.elevance_skills.swiggy_backend_clone.dto.UserResponse;
import com.elevance_skills.swiggy_backend_clone.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(
            @Valid @RequestBody UserRequest request) {

        return userService.createUser(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {

        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(
            @PathVariable String id) {

        return userService.getUserById(id);
    }
}
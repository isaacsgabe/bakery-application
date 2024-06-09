package com.reshipies.project.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        // Perform health check logic here
        // For example, check if critical services are running
        // If everything is okay, return HTTP status 200 OK
        return ResponseEntity.ok("Health check passed");
    }
}


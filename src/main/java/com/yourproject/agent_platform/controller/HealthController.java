package com.yourproject.agent_platform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check controller
 * GET /api/health -> returns "ok" if the service is running.
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public String getHealth() {
        return "ok";
    }
}

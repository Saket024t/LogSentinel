package com.logsentinel.gateway;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
public class LogQueryController {

    @GetMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'OPERATOR', 'ADMIN')")
    public List<Map<String, String>> getAllLogs() {
        return List.of(Map.of("message", "Dummy log data", "level", "INFO"));
    }

    @GetMapping("/anomalies")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    public List<Map<String, String>> getAnomalies() {
        return List.of(Map.of("message", "Dummy anomaly data", "level", "ERROR"));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> getAdminStats() {
        return Map.of("stats", "All system stats");
    }
}

package com.micorregimiento.micorregimiento.Config.controllers;

import com.micorregimiento.micorregimiento.Config.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    private final DatabaseService service;

    @Autowired
    public DatabaseController(DatabaseService service) {
        this.service = service;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        boolean connected = service.isConnected();
        return ResponseEntity.ok(Map.of(
                "status", connected ? "connected" : "disconnected",
                "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testDatabase() {
        try (Connection conn = service.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT message, created_at FROM test_connection")) {

            List<Map<String, Object>> results = new ArrayList<>();
            while (rs.next()) {
                results.add(Map.of(
                        "message", rs.getString("message"),
                        "created_at", rs.getTimestamp("created_at").toString()
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", results,
                    "count", results.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}
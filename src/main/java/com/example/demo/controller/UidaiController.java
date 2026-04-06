package com.example.demo.controller;

import com.example.demo.service.UidaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/uidai")
public class UidaiController {

    @Autowired
    private UidaiService uidaiService;

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyAadhar(@RequestParam String aadharNumber) {
        boolean isValid = uidaiService.verifyAadhar(aadharNumber);

        Map<String, Object> response = new HashMap<>();
        if (isValid) {
            response.put("success", true);
            response.put("message", "Aadhaar verified successfully with UIDAI");
        } else {
            response.put("success", false);
            response.put("message", "Invalid Aadhaar number. Please enter 12 digits.");
        }

        return ResponseEntity.ok(response);
    }
}

package com.pst.support.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pst.support.service.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/decode-token")
    public ResponseEntity<?> decodeToken(@RequestParam String token) {
        String email = jwtService.getEmailFromToken(token);
        String role = jwtService.getRoleFromToken(token);

        Map<String, String> response = new HashMap<>();
        response.put("email", email);
        response.put("role", role);

        // Retourner l'objet dans une ResponseEntity
        return ResponseEntity.ok(response);
    }
}
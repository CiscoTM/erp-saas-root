package es.tk3.controller;

import es.tk3.dto.AuthResponse;
import es.tk3.dto.LoginRequest;
import es.tk3.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String token = authService.login(req.getUsername(), req.getPassword(), req.getTenantId());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
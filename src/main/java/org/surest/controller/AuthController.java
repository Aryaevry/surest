package org.surest.controller;

import org.springframework.security.core.Authentication;
import org.surest.dto.AuthRequest;
import org.surest.dto.AuthResponse;
import org.surest.entity.User;
import org.surest.repository.UserRepository;
import org.surest.security.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            User user = userRepository.findByUsername(authRequest.getUsername());
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().getName());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).build();
        }
    }


//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
//        String username = authRequest.getUsername();
//        String password = authRequest.getPassword();
//
//        // Hardcoded credentials
//        if ("admin".equals(username) && "admin123".equals(password)) {
//            // Hardcode role
//            String role = "ADMIN";
//            String token = jwtUtil.generateToken(username, role);
//            return ResponseEntity.ok(new AuthResponse(token));
//        } else if ("user".equals(username) && "user123".equals(password)) {
//            String role = "USER";
//            String token = jwtUtil.generateToken(username, role);
//            return ResponseEntity.ok(new AuthResponse(token));
//        }
//
//        // If credentials don't match
//        return ResponseEntity.status(401).build();
//    }

}

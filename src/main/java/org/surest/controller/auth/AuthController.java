package org.surest.controller.auth;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.surest.dto.AuthRequest;
import org.surest.dto.AuthResponse;
import org.surest.entity.User;
import org.surest.repository.UserRepository;
import org.surest.security.util.JwtUtil;

/**
 * REST controller responsible for authentication endpoints.
 * <p>
 * Provides login functionality that authenticates a user using Spring Security's
 * {@link AuthenticationManager} and issues a JWT token upon successful login.
 * SLF4J logging is used to track authentication attempts and failures.
 */
@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs the AuthController with required dependencies.
     *
     * @param authenticationManager Spring Security authentication manager
     * @param jwtUtil               utility for generating JWT tokens
     * @param userRepository        repository to retrieve user details
     * @param passwordEncoder       encoder for hashing/verifying passwords
     */
    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates a user based on username and password.
     * <p>
     * Receives an {@link AuthRequest} containing the username and password,
     * attempts authentication using Spring Security, and if successful, returns
     * a JWT token wrapped in {@link AuthResponse}. Logs both successful and
     * failed login attempts.
     *
     * @param authRequest the login request containing username and password
     * @return {@link ResponseEntity} containing {@link AuthResponse} with JWT token
     *         if authentication is successful, or 401 Unauthorized if failed
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            logger.info("Login attempt for user: {}", authRequest.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            User user = userRepository.findByUsername(authRequest.getUsername());

            // Generate JWT token with username and role
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().getName());

            logger.info("Login successful for user: {}", authRequest.getUsername());

            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException e) {
            logger.warn("Login failed for user: {} - invalid credentials", authRequest.getUsername());
            return ResponseEntity.status(401).build();
        }
    }
}

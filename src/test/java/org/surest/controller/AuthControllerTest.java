package org.surest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.surest.controller.auth.AuthController;
import org.surest.dto.AuthRequest;
import org.surest.dto.AuthResponse;
import org.surest.entity.Role;
import org.surest.entity.User;
import org.surest.repository.UserRepository;
import org.surest.security.util.JwtUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtUtil = mock(JwtUtil.class);
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        authController = new AuthController(authenticationManager, jwtUtil, userRepository, passwordEncoder);
    }

    @Test
    void testLogin_Success() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setUsername("john");
        request.setPassword("password");

        Role role = new Role();
        role.setName("ADMIN");

        User user = new User();
        user.setUsername("john");
        user.setPasswordHash("hashed");
        user.setRole(role);

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername("john")).thenReturn(user);
        when(jwtUtil.generateToken("john", "ADMIN")).thenReturn("token123");

        // Act
        ResponseEntity<AuthResponse> response = authController.login(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("token123", response.getBody().getToken());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByUsername("john");
        verify(jwtUtil, times(1)).generateToken("john", "ADMIN");
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setUsername("john");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act
        ResponseEntity<AuthResponse> response = authController.login(request);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        assertEquals(null, response.getBody());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByUsername(anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }
}

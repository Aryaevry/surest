package org.surest.security.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filter that intercepts every request to:
 * 1. Extract JWT from the Authorization header
 * 2. Validate the token
 * 3. Extract username and role
 * 4. Set authentication in the SecurityContext
 *
 * Extends OncePerRequestFilter to ensure it runs once per request.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // Inject JwtUtil using constructor
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Extract the Authorization header
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String role = null;
        String jwt = null;

        // 2. Check if header starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Remove "Bearer " prefix
            try {
                // 3. Decode token and extract claims
                username = jwtUtil.extractUsername(jwt);
                role = jwtUtil.extractRole(jwt);
            } catch (Exception e) {
              throw new ServletException("Invalid JWT token");
            }
        }

        // 4. If username is valid and authentication context is empty
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 5. Validate the token (expiration and username)
            if (jwtUtil.validateToken(jwt, username)) {

                // 6. Create Spring Security authorities from role
                var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

                // 7. Build authentication token with username and authorities
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                // 8. Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 9. Continue filter chain
        filterChain.doFilter(request, response);
    }
}

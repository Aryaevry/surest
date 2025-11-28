package org.surest.serviceimpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.surest.entity.User;
import org.surest.repository.UserRepository;

import java.util.List;

/**
 * Implementation of {@link UserDetailsService} used by Spring Security
 * to load user-specific data during authentication.
 * <p>
 * This service fetches {@link User} entities from the database and
 * converts them into {@link UserDetails} objects that Spring Security can use.
 * SLF4J logging is used to track user lookup attempts and outcomes.
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {


    private final UserRepository userRepository;

    /**
     * Constructs a {@link UserDetailsServiceImpl} with the required {@link UserRepository}.
     *
     * @param userRepository repository used to fetch user data
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their username.
     * <p>
     * This method is called by Spring Security during authentication.
     * If the user is not found, a {@link UsernameNotFoundException} is thrown.
     *
     * @param username the username of the user to load
     * @return a {@link UserDetails} object containing username, password, and roles
     * @throws UsernameNotFoundException if no user with the given username exists
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by username: {}", username);

        User user = userRepository.findByUsername(username);

        if (user == null) {
            log.warn("User not found with username: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        log.info("User found: {}", username);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()))
        );
    }
}

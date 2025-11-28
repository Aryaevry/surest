package org.surest.dbseeder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.surest.entity.Role;
import org.surest.entity.User;
import org.surest.repository.RoleRepository;
import org.surest.repository.UserRepository;

/**
 * Service responsible for seeding initial roles and users into the database.
 * <p>
 * This service ensures that essential roles ("ADMIN" and "USER") are present
 * in the database and creates default admin and regular users if none exist.
 * <p>
 * It uses {@link PasswordEncoder} to securely hash passwords before saving
 * users. SLF4J logging is used to track seeding operations.
 */
@Service
@Slf4j
public class SeedService {


    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN = "ADMIN";
    private static final String USER = "USER";

    /**
     * Constructs a SeedService with required repositories and password encoder.
     *
     * @param roleRepository  repository for managing Role entities
     * @param userRepository  repository for managing User entities
     * @param passwordEncoder encoder for hashing user passwords
     */
    public SeedService(RoleRepository roleRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Seeds roles and users into the database.
     * <p>
     * This method first seeds roles if they do not exist, then seeds users
     * if the users table is empty.
     */
    public void seed() {
        seedRoles();
        seedUsers();
    }

    /**
     * Seeds the essential roles ("ADMIN" and "USER") into the database
     * if they do not already exist.
     */
    private void seedRoles() {
        if (roleRepository.findByName(ADMIN) == null) {
            Role adminRole = Role.builder()
                    .name(ADMIN)
                    .build();
            roleRepository.save(adminRole);
            log.info("Seeded ROLE: {}", ADMIN);
        }

        if (roleRepository.findByName(USER) == null) {
            Role userRole = Role.builder()
                    .name(USER)
                    .build();
            roleRepository.save(userRole);
            log.info("Seeded ROLE: {}", USER);
        }
    }

    /**
     * Seeds default users into the database if the users table is empty.
     * <p>
     * Creates a default admin user with username "admin" and a default regular
     * user with username "user". Passwords are hashed using {@link PasswordEncoder}.
     */
    private void seedUsers() {
        if (userRepository.count() > 0) {
            log.info("Users already exist. Skipping user seeding.");
            return;
        }

        Role adminRole = roleRepository.findByName(ADMIN);
        Role userRole = roleRepository.findByName(USER);

        User admin = User.builder()
                .username("admin")
                .passwordHash(passwordEncoder.encode("admin123"))
                .role(adminRole)
                .build();

        User user = User.builder()
                .username("user")
                .passwordHash(passwordEncoder.encode("user123"))
                .role(userRole)
                .build();

        userRepository.save(admin);
        log.info("Seeded USER: admin");

        userRepository.save(user);
        log.info("Seeded USER: user");
    }
}

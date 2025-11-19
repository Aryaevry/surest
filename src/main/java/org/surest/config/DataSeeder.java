package org.surest.config;

import org.surest.entity.Role;
import org.surest.entity.User;
import org.surest.repository.RoleRepository;
import org.surest.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findAll().isEmpty()) {
            Role adminRole = roleRepository.save(new Role(null, "ADMIN"));
            Role userRole = roleRepository.save(new Role(null, "USER"));

            userRepository.save(new User(null, "admin", passwordEncoder.encode("admin123"), adminRole));
            userRepository.save(new User(null, "user", passwordEncoder.encode("user123"), userRole));
        }
    }
}

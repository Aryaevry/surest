package org.surest.config;

import org.surest.entity.Role;
import org.surest.entity.User;
import org.surest.repository.RoleRepository;
import org.surest.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner seedDatabase(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {
            // Seed roles
            if (roleRepository.findByName("ADMIN") == null) {
                Role adminRole = new Role();
                adminRole.setName("ADMIN");
                roleRepository.save(adminRole);
            }

            if (roleRepository.findByName("USER") == null) {
                Role userRole = new Role();
                userRole.setName("USER");
                roleRepository.save(userRole);
            }

            // Seed users
            if (userRepository.findAll().isEmpty()) {
                Role adminRole = roleRepository.findByName("ADMIN");
                Role userRole = roleRepository.findByName("USER");

                User admin = new User();
                admin.setUsername("admin");
                admin.setPasswordHash("{noop}admin123"); // For simplicity
                admin.setRole(adminRole);
                userRepository.save(admin);

                User user = new User();
                user.setUsername("user");
                user.setPasswordHash("{noop}user123");
                user.setRole(userRole);
                userRepository.save(user);
            }
        };
    }
}

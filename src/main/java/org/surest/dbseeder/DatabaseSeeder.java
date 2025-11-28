package org.surest.dbseeder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class responsible for triggering database seeding
 * at application startup.
 * <p>
 * This class delegates the actual seeding logic to {@link SeedService}
 * and logs the progress using SLF4J. It ensures that essential roles
 * and default users exist in the database when the application starts.
 */
@Configuration
@Slf4j
public class DatabaseSeeder {

    private final SeedService seedService;

    /**
     * Constructs a DatabaseSeeder with a reference to the {@link SeedService}.
     *
     * @param seedService the service that contains the database seeding logic
     */
    public DatabaseSeeder(SeedService seedService) {
        this.seedService = seedService;
    }

    /**
     * Returns a {@link CommandLineRunner} bean that executes database seeding
     * when the Spring Boot application starts.
     * <p>
     * This method logs the start and completion of the seeding process, and
     * catches any exceptions that may occur during seeding to log them as errors.
     *
     * @return a CommandLineRunner that triggers seeding via {@link SeedService}
     */
    @Bean
    public CommandLineRunner seedDatabase() {
        return args -> {
            log.info("Starting database seeding...");

            try {
                seedService.seed();
                log.info("Database seeding completed successfully.");
            } catch (Exception e) {
                log.error("Error occurred during database seeding", e);
            }
        };
    }
}

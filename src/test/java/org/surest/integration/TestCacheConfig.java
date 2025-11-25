package org.surest.integration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class TestCacheConfig {
    // Spring Boot automatically picks up the Redis settings from application.properties
}

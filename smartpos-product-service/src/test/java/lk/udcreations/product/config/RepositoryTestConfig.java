package lk.udcreations.product.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "lk.udcreations.product.repository")
@EntityScan(basePackages = "lk.udcreations.product.entity")
@ActiveProfiles("test")
public class RepositoryTestConfig {
    // This is a minimal configuration for repository tests
    // No need for @SpringBootApplication which would cause conflicts
}
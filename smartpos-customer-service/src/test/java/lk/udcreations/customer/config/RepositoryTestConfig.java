package lk.udcreations.customer.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "lk.udcreations.customer.repository")
@EntityScan(basePackages = "lk.udcreations.customer.entity")
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.sql.init.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
public class RepositoryTestConfig {
    // This is a minimal configuration for repository tests
    // No need for @SpringBootApplication which would cause conflicts
}

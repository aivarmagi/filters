package ee.aivar.filters.configuration;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.flywaydb.core.Flyway;

@Configuration
public class FlywayConfiguration {

    @Bean
    public FlywayMigrationStrategy cleanMigrateStrategy() {
        return flyway -> {
            // do nothing, wait for hibernate to create tables
        };
    }

    @Bean
    public ApplicationRunner runner(Flyway flyway) {
        // run migration after hibernate execution
        return args -> flyway.migrate();
    }
}

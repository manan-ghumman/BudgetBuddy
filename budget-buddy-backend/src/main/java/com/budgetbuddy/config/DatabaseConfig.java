package com.budgetbuddy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            logger.info("DATABASE_URL detected — configuring PostgreSQL DataSource.");
            try {
                URI uri = new URI(databaseUrl);

                String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + uri.getPort() + uri.getPath();
                String username = uri.getUserInfo().split(":")[0];
                String password = uri.getUserInfo().split(":")[1];

                return DataSourceBuilder.create()
                        .url(jdbcUrl)
                        .username(username)
                        .password(password)
                        .driverClassName("org.postgresql.Driver")
                        .build();
            } catch (Exception e) {
                logger.error("Failed to parse DATABASE_URL, falling back to defaults.", e);
            }
        }

        logger.info("No DATABASE_URL found — using default DataSource from application.properties (H2).");
        return DataSourceBuilder.create()
                .url("jdbc:h2:file:./data/budget_buddy;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH")
                .username("sa")
                .password("")
                .driverClassName("org.h2.Driver")
                .build();
    }
}

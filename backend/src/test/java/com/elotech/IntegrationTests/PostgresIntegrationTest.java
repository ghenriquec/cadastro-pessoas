package com.elotech.IntegrationTests;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Boots the whole application against a throwaway PostgreSQL container.
 * Flyway creates the schema and Hibernate validates the entities against it,
 * so these tests also fail if a migration and the entities drift apart.
 */
@SpringBootTest
@AutoConfigureMockMvc
abstract class PostgresIntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    static {
        // Started once and shared by every integration test class; Testcontainers removes it after the JVM exits.
        POSTGRES.start();
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limparBanco() {
        jdbcTemplate.execute("TRUNCATE TABLE contatos, pessoas RESTART IDENTITY CASCADE");
    }

    protected static String json(String singleQuoted) {
        return singleQuoted.replace('\'', '"');
    }
}

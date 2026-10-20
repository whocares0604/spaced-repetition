package io.github.whocares0604.spacedrepetition;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class SpacedRepetitionApplicationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    void contextLoadsAndAppliesMigrations() {
        List<String> appliedVersions = jdbcClient
                .sql("SELECT version FROM flyway_schema_history WHERE success ORDER BY installed_rank")
                .query(String.class)
                .list();

        assertThat(appliedVersions).containsExactly("1", "2", "3");
    }
}

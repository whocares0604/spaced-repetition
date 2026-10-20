package io.github.whocares0604.spacedrepetition;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class SpacedRepetitionApplicationTest {

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

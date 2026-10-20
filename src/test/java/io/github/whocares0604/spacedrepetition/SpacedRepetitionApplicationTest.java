package io.github.whocares0604.spacedrepetition;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;
import java.util.Map;

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

        assertThat(appliedVersions).containsExactly("1", "2", "3", "4");
    }

    @Test
    void newCardGetsDefaultReviewFields() {
        Long deckId = jdbcClient.sql("INSERT INTO deck (name) VALUES ('Defaults') RETURNING id")
                .query(Long.class)
                .single();

        Map<String, Object> card = jdbcClient.sql("""
                        INSERT INTO card (deck_id, front, back)
                        VALUES (:deckId, 'front', 'back')
                        RETURNING repetitions, ease_factor
                        """)
                .param("deckId", deckId)
                .query()
                .singleRow();

        assertThat(card)
                .containsEntry("repetitions", 0)
                .containsEntry("ease_factor", 2.5);
    }
}

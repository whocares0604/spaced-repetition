package io.github.whocares0604.spacedrepetition.deck;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DeckRepository {

    private final JdbcClient jdbcClient;

    public DeckRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Deck create(String name) {
        return jdbcClient.sql("""
                        INSERT INTO deck (name)
                        VALUES (:name)
                        RETURNING id, name, created_at
                        """)
                .param("name", name)
                .query(Deck.class)
                .single();
    }

    public List<Deck> findAll() {
        return jdbcClient.sql("SELECT id, name, created_at FROM deck ORDER BY id")
                .query(Deck.class)
                .list();
    }

    public Optional<Deck> findById(Long id) {
        return jdbcClient.sql("SELECT id, name, created_at FROM deck WHERE id = :id")
                .param("id", id)
                .query(Deck.class)
                .optional();
    }

    public Optional<Deck> update(Long id, String name) {
        return jdbcClient.sql("""
                        UPDATE deck
                        SET name = :name
                        WHERE id = :id
                        RETURNING id, name, created_at
                        """)
                .param("id", id)
                .param("name", name)
                .query(Deck.class)
                .optional();
    }

    public boolean deleteById(Long id) {
        int deletedRows = jdbcClient.sql("DELETE FROM deck WHERE id = :id")
                .param("id", id)
                .update();
        return deletedRows > 0;
    }
}

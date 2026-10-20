package io.github.whocares0604.spacedrepetition.card;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CardRepository {

    private final JdbcClient jdbcClient;

    public CardRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Card create(Long deckId, String front, String back) {
        return jdbcClient.sql("""
                        INSERT INTO card (deck_id, front, back)
                        VALUES (:deckId, :front, :back)
                        RETURNING id, deck_id, front, back, created_at, due_at, interval_days
                        """)
                .param("deckId", deckId)
                .param("front", front)
                .param("back", back)
                .query(Card.class)
                .single();
    }

    public List<Card> findByDeckId(Long deckId) {
        return jdbcClient.sql("""
                        SELECT id, deck_id, front, back, created_at, due_at, interval_days
                        FROM card
                        WHERE deck_id = :deckId
                        ORDER BY id
                        """)
                .param("deckId", deckId)
                .query(Card.class)
                .list();
    }

    public Optional<Card> findById(Long id) {
        return jdbcClient.sql("""
                        SELECT id, deck_id, front, back, created_at, due_at, interval_days
                        FROM card
                        WHERE id = :id
                        """)
                .param("id", id)
                .query(Card.class)
                .optional();
    }

    public Optional<Card> update(Long id, String front, String back) {
        return jdbcClient.sql("""
                        UPDATE card
                        SET front = :front, back = :back
                        WHERE id = :id
                        RETURNING id, deck_id, front, back, created_at, due_at, interval_days
                        """)
                .param("id", id)
                .param("front", front)
                .param("back", back)
                .query(Card.class)
                .optional();
    }

    public boolean deleteById(Long id) {
        int deletedRows = jdbcClient.sql("DELETE FROM card WHERE id = :id")
                .param("id", id)
                .update();
        return deletedRows > 0;
    }

    public void deleteByDeckId(Long deckId) {
        jdbcClient.sql("DELETE FROM card WHERE deck_id = :deckId")
                .param("deckId", deckId)
                .update();
    }
}

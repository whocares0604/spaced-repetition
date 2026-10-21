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
                        RETURNING id, deck_id, front, back, created_at, due_at, interval_days, repetitions, ease_factor
                        """)
                .param("deckId", deckId)
                .param("front", front)
                .param("back", back)
                .query(Card.class)
                .single();
    }

    public List<Card> findByDeckId(Long deckId) {
        return jdbcClient.sql("""
                        SELECT id, deck_id, front, back, created_at, due_at, interval_days, repetitions, ease_factor
                        FROM card
                        WHERE deck_id = :deckId
                        ORDER BY id
                        """)
                .param("deckId", deckId)
                .query(Card.class)
                .list();
    }

    public List<Card> findDueByDeckId(Long deckId) {
        return jdbcClient.sql("""
                        SELECT id, deck_id, front, back, created_at, due_at, interval_days, repetitions, ease_factor
                        FROM card
                        WHERE deck_id = :deckId AND due_at <= CURRENT_TIMESTAMP
                        ORDER BY due_at, id
                        """)
                .param("deckId", deckId)
                .query(Card.class)
                .list();
    }

    public Optional<Card> findById(Long id) {
        return jdbcClient.sql("""
                        SELECT id, deck_id, front, back, created_at, due_at, interval_days, repetitions, ease_factor
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
                        RETURNING id, deck_id, front, back, created_at, due_at, interval_days, repetitions, ease_factor
                        """)
                .param("id", id)
                .param("front", front)
                .param("back", back)
                .query(Card.class)
                .optional();
    }

    public Card updateReviewState(Long id, int repetitions, int intervalDays, double easeFactor) {
        return jdbcClient.sql("""
                        UPDATE card
                        SET repetitions = :repetitions,
                            interval_days = :intervalDays,
                            ease_factor = :easeFactor,
                            due_at = CURRENT_TIMESTAMP + make_interval(days => :intervalDays)
                        WHERE id = :id
                        RETURNING id, deck_id, front, back, created_at, due_at, interval_days, repetitions, ease_factor
                        """)
                .param("id", id)
                .param("repetitions", repetitions)
                .param("intervalDays", intervalDays)
                .param("easeFactor", easeFactor)
                .query(Card.class)
                .single();
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

package io.github.whocares0604.spacedrepetition.card;

import java.time.OffsetDateTime;

public record Card(
        Long id,
        Long deckId,
        String front,
        String back,
        OffsetDateTime createdAt,
        OffsetDateTime dueAt,
        int intervalDays,
        int repetitions,
        double easeFactor
) {
}

package io.github.whocares0604.spacedrepetition.card;

import java.time.OffsetDateTime;

public record CardResponse(
        Long id,
        Long deckId,
        String front,
        String back,
        OffsetDateTime createdAt,
        OffsetDateTime dueAt,
        int intervalDays
) {

    public static CardResponse from(Card card) {
        return new CardResponse(
                card.id(),
                card.deckId(),
                card.front(),
                card.back(),
                card.createdAt(),
                card.dueAt(),
                card.intervalDays()
        );
    }
}

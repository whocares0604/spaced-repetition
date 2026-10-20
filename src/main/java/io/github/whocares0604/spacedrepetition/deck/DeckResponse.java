package io.github.whocares0604.spacedrepetition.deck;

import java.time.OffsetDateTime;

public record DeckResponse(Long id, String name, OffsetDateTime createdAt) {

    public static DeckResponse from(Deck deck) {
        return new DeckResponse(deck.id(), deck.name(), deck.createdAt());
    }
}

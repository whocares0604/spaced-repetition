package io.github.whocares0604.spacedrepetition.deck;

import java.time.OffsetDateTime;

public record Deck(Long id, String name, OffsetDateTime createdAt) {
}

package io.github.whocares0604.spacedrepetition.deck;

import io.github.whocares0604.spacedrepetition.api.NotFoundException;

public class DeckNotFoundException extends NotFoundException {

    public DeckNotFoundException(Long id) {
        super("Deck with id " + id + " not found");
    }
}

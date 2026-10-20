package io.github.whocares0604.spacedrepetition.card;

import io.github.whocares0604.spacedrepetition.api.NotFoundException;

public class CardNotFoundException extends NotFoundException {

    public CardNotFoundException(Long id) {
        super("Card with id " + id + " not found");
    }
}

package io.github.whocares0604.spacedrepetition.card;

import io.github.whocares0604.spacedrepetition.deck.DeckNotFoundException;
import io.github.whocares0604.spacedrepetition.deck.DeckRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    public CardService(CardRepository cardRepository, DeckRepository deckRepository) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
    }

    public Card create(Long deckId, String front, String back) {
        checkDeckExists(deckId);
        return cardRepository.create(deckId, front, back);
    }

    public List<Card> findByDeckId(Long deckId) {
        checkDeckExists(deckId);
        return cardRepository.findByDeckId(deckId);
    }

    public Card findById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
    }

    public Card update(Long id, String front, String back) {
        return cardRepository.update(id, front, back)
                .orElseThrow(() -> new CardNotFoundException(id));
    }

    public void delete(Long id) {
        if (!cardRepository.deleteById(id)) {
            throw new CardNotFoundException(id);
        }
    }

    private void checkDeckExists(Long deckId) {
        if (deckRepository.findById(deckId).isEmpty()) {
            throw new DeckNotFoundException(deckId);
        }
    }
}

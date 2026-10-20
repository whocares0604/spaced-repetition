package io.github.whocares0604.spacedrepetition.deck;

import io.github.whocares0604.spacedrepetition.card.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeckService {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;

    public DeckService(DeckRepository deckRepository, CardRepository cardRepository) {
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
    }

    public Deck create(String name) {
        return deckRepository.create(name);
    }

    public List<Deck> findAll() {
        return deckRepository.findAll();
    }

    public Deck findById(Long id) {
        return deckRepository.findById(id)
                .orElseThrow(() -> new DeckNotFoundException(id));
    }

    public Deck update(Long id, String name) {
        return deckRepository.update(id, name)
                .orElseThrow(() -> new DeckNotFoundException(id));
    }

    @Transactional
    public void delete(Long id) {
        cardRepository.deleteByDeckId(id);
        if (!deckRepository.deleteById(id)) {
            throw new DeckNotFoundException(id);
        }
    }
}

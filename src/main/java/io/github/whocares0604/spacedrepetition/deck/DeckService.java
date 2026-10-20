package io.github.whocares0604.spacedrepetition.deck;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeckService {

    private final DeckRepository deckRepository;

    public DeckService(DeckRepository deckRepository) {
        this.deckRepository = deckRepository;
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

    public void delete(Long id) {
        if (!deckRepository.deleteById(id)) {
            throw new DeckNotFoundException(id);
        }
    }
}

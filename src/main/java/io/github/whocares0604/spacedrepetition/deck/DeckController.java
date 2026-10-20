package io.github.whocares0604.spacedrepetition.deck;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/decks")
public class DeckController {

    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    @PostMapping
    public ResponseEntity<DeckResponse> create(@Valid @RequestBody DeckRequest request) {
        Deck deck = deckService.create(request.name());
        URI location = URI.create("/api/decks/" + deck.id());
        return ResponseEntity.created(location).body(DeckResponse.from(deck));
    }

    @GetMapping
    public List<DeckResponse> findAll() {
        return deckService.findAll().stream()
                .map(DeckResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public DeckResponse findById(@PathVariable Long id) {
        return DeckResponse.from(deckService.findById(id));
    }

    @PutMapping("/{id}")
    public DeckResponse update(@PathVariable Long id, @Valid @RequestBody DeckRequest request) {
        return DeckResponse.from(deckService.update(id, request.name()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deckService.delete(id);
    }
}

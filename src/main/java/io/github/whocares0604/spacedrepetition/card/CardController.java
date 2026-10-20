package io.github.whocares0604.spacedrepetition.card;

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
@RequestMapping("/api")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/decks/{deckId}/cards")
    public ResponseEntity<CardResponse> create(@PathVariable Long deckId, @Valid @RequestBody CardRequest request) {
        Card card = cardService.create(deckId, request.front(), request.back());
        URI location = URI.create("/api/cards/" + card.id());
        return ResponseEntity.created(location).body(CardResponse.from(card));
    }

    @GetMapping("/decks/{deckId}/cards")
    public List<CardResponse> findByDeckId(@PathVariable Long deckId) {
        return cardService.findByDeckId(deckId).stream()
                .map(CardResponse::from)
                .toList();
    }

    @GetMapping("/cards/{id}")
    public CardResponse findById(@PathVariable Long id) {
        return CardResponse.from(cardService.findById(id));
    }

    @PutMapping("/cards/{id}")
    public CardResponse update(@PathVariable Long id, @Valid @RequestBody CardRequest request) {
        return CardResponse.from(cardService.update(id, request.front(), request.back()));
    }

    @DeleteMapping("/cards/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        cardService.delete(id);
    }
}

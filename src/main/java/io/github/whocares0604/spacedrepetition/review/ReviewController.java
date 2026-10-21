package io.github.whocares0604.spacedrepetition.review;

import io.github.whocares0604.spacedrepetition.card.CardResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/decks/{deckId}/cards/due")
    public List<CardResponse> findDueCards(@PathVariable Long deckId) {
        return reviewService.findDueCards(deckId).stream()
                .map(CardResponse::from)
                .toList();
    }

    @PostMapping("/cards/{id}/review")
    public CardResponse review(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        return CardResponse.from(reviewService.review(id, request.grade()));
    }
}

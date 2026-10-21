package io.github.whocares0604.spacedrepetition.review;

import io.github.whocares0604.spacedrepetition.card.Card;
import io.github.whocares0604.spacedrepetition.card.CardNotFoundException;
import io.github.whocares0604.spacedrepetition.card.CardRepository;
import io.github.whocares0604.spacedrepetition.deck.DeckNotFoundException;
import io.github.whocares0604.spacedrepetition.deck.DeckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final Sm2Scheduler scheduler;

    public ReviewService(CardRepository cardRepository, DeckRepository deckRepository, Sm2Scheduler scheduler) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
        this.scheduler = scheduler;
    }

    public List<Card> findDueCards(Long deckId) {
        if (deckRepository.findById(deckId).isEmpty()) {
            throw new DeckNotFoundException(deckId);
        }
        return cardRepository.findDueByDeckId(deckId);
    }

    @Transactional
    public Card review(Long cardId, ReviewGrade grade) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        ReviewState current = new ReviewState(card.repetitions(), card.intervalDays(), card.easeFactor());
        ReviewState next = scheduler.next(current, grade);

        return cardRepository.updateReviewState(cardId, next.repetitions(), next.intervalDays(), next.easeFactor());
    }
}

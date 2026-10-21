package io.github.whocares0604.spacedrepetition;

import io.github.whocares0604.spacedrepetition.card.CardRequest;
import io.github.whocares0604.spacedrepetition.card.CardResponse;
import io.github.whocares0604.spacedrepetition.deck.DeckRequest;
import io.github.whocares0604.spacedrepetition.deck.DeckResponse;
import io.github.whocares0604.spacedrepetition.review.ReviewGrade;
import io.github.whocares0604.spacedrepetition.review.ReviewRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs the whole review flow over real HTTP: create a deck, add a card,
 * get due cards, review the card and check that its due date moved.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Import(TestcontainersConfiguration.class)
class ReviewFlowEndToEndTest {

    @Autowired
    private RestTestClient client;

    @Test
    void reviewMovesCardOutOfDueListAndShiftsDueDate() {
        DeckResponse deck = client.post().uri("/api/decks")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new DeckRequest("End-to-end deck"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(DeckResponse.class)
                .returnResult()
                .getResponseBody();

        CardResponse card = client.post().uri("/api/decks/{deckId}/cards", deck.id())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CardRequest("perro", "dog"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CardResponse.class)
                .returnResult()
                .getResponseBody();

        List<CardResponse> dueBefore = findDueCards(deck.id());
        assertThat(dueBefore).extracting(CardResponse::id).containsExactly(card.id());

        CardResponse reviewed = client.post().uri("/api/cards/{id}/review", card.id())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ReviewRequest(ReviewGrade.GOOD))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CardResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(reviewed.repetitions()).isEqualTo(1);
        assertThat(reviewed.intervalDays()).isEqualTo(1);
        assertThat(reviewed.dueAt()).isAfter(card.dueAt().plusHours(23));

        List<CardResponse> dueAfter = findDueCards(deck.id());
        assertThat(dueAfter).isEmpty();
    }

    private List<CardResponse> findDueCards(Long deckId) {
        return client.get().uri("/api/decks/{deckId}/cards/due", deckId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<CardResponse>>() {
                })
                .returnResult()
                .getResponseBody();
    }
}

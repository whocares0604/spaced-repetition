package io.github.whocares0604.spacedrepetition.review;

import io.github.whocares0604.spacedrepetition.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    void newCardIsDueAndDisappearsFromDueListAfterReview() throws Exception {
        String deckLocation = createDeck("Due deck");
        String cardLocation = createCard(deckLocation);

        mockMvc.perform(get(deckLocation + "/cards/due"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].front").value("hola"));

        review(cardLocation, "GOOD")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repetitions").value(1))
                .andExpect(jsonPath("$.intervalDays").value(1))
                .andExpect(jsonPath("$.easeFactor").value(2.5));

        mockMvc.perform(get(deckLocation + "/cards/due"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void reviewMovesDueDateByIntervalDays() throws Exception {
        String deckLocation = createDeck("Interval deck");
        String cardLocation = createCard(deckLocation);
        Long cardId = idFrom(cardLocation);

        review(cardLocation, "EASY")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intervalDays").value(2));

        Boolean dueInTwoDays = jdbcClient.sql("""
                        SELECT due_at BETWEEN CURRENT_TIMESTAMP + INTERVAL '47 hours'
                                          AND CURRENT_TIMESTAMP + INTERVAL '49 hours'
                        FROM card
                        WHERE id = :id
                        """)
                .param("id", cardId)
                .query(Boolean.class)
                .single();
        assertThat(dueInTwoDays).isTrue();
    }

    @Test
    void againResetsRepetitions() throws Exception {
        String deckLocation = createDeck("Again deck");
        String cardLocation = createCard(deckLocation);

        review(cardLocation, "GOOD");
        review(cardLocation, "GOOD")
                .andExpect(jsonPath("$.repetitions").value(2))
                .andExpect(jsonPath("$.intervalDays").value(6));

        review(cardLocation, "AGAIN")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repetitions").value(0))
                .andExpect(jsonPath("$.intervalDays").value(1))
                .andExpect(jsonPath("$.easeFactor").value(2.3));
    }

    @Test
    void rejectsMissingOrUnknownGrade() throws Exception {
        String cardLocation = createCard(createDeck("Grade deck"));

        mockMvc.perform(post(cardLocation + "/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.grade").value("must not be null"));

        review(cardLocation, "PERFECT")
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsNotFoundForUnknownDeckOrCard() throws Exception {
        mockMvc.perform(get("/api/decks/999999/cards/due"))
                .andExpect(status().isNotFound());

        review("/api/cards/999999", "GOOD")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Card with id 999999 not found"));
    }

    private ResultActions review(String cardLocation, String grade) throws Exception {
        return mockMvc.perform(post(cardLocation + "/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"grade": "%s"}
                        """.formatted(grade)));
    }

    private String createDeck(String name) throws Exception {
        return mockMvc.perform(post("/api/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "%s"}
                                """.formatted(name)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");
    }

    private String createCard(String deckLocation) throws Exception {
        return mockMvc.perform(post(deckLocation + "/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"front": "hola", "back": "hello"}
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");
    }

    private Long idFrom(String location) {
        return Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
    }
}

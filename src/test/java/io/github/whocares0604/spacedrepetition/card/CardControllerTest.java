package io.github.whocares0604.spacedrepetition.card;

import io.github.whocares0604.spacedrepetition.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsAndReadsCard() throws Exception {
        String deckLocation = createDeck("Spanish");
        String cardLocation = createCard(deckLocation, "hola", "hello");

        mockMvc.perform(get(cardLocation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.front").value("hola"))
                .andExpect(jsonPath("$.back").value("hello"))
                .andExpect(jsonPath("$.dueAt").value(notNullValue()))
                .andExpect(jsonPath("$.intervalDays").value(0))
                .andExpect(jsonPath("$.repetitions").value(0))
                .andExpect(jsonPath("$.easeFactor").value(2.5));
    }

    @Test
    void listsCardsOfDeck() throws Exception {
        String deckLocation = createDeck("German");
        createCard(deckLocation, "eins", "one");
        createCard(deckLocation, "zwei", "two");

        mockMvc.perform(get(deckLocation + "/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].front").value("eins"))
                .andExpect(jsonPath("$[1].front").value("zwei"));
    }

    @Test
    void updatesCard() throws Exception {
        String deckLocation = createDeck("French");
        String cardLocation = createCard(deckLocation, "chat", "dog");

        mockMvc.perform(put(cardLocation)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"front": "chat", "back": "cat"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.back").value("cat"));
    }

    @Test
    void deletesCard() throws Exception {
        String deckLocation = createDeck("Italian");
        String cardLocation = createCard(deckLocation, "ciao", "hi");

        mockMvc.perform(delete(cardLocation))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(cardLocation))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletesCardsTogetherWithDeck() throws Exception {
        String deckLocation = createDeck("Polish");
        String cardLocation = createCard(deckLocation, "tak", "yes");

        mockMvc.perform(delete(deckLocation))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(cardLocation))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsNotFoundForUnknownDeckOrCard() throws Exception {
        mockMvc.perform(post("/api/decks/999999/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"front": "a", "back": "b"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Deck with id 999999 not found"));

        mockMvc.perform(get("/api/decks/999999/cards"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/cards/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Card with id 999999 not found"));
    }

    @Test
    void rejectsBlankFrontAndBack() throws Exception {
        String deckLocation = createDeck("Empty cards");

        mockMvc.perform(post(deckLocation + "/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"front": "", "back": " "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.front").value("must not be blank"))
                .andExpect(jsonPath("$.errors.back").value("must not be blank"));
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

    private String createCard(String deckLocation, String front, String back) throws Exception {
        return mockMvc.perform(post(deckLocation + "/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"front": "%s", "back": "%s"}
                                """.formatted(front, back)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andReturn()
                .getResponse()
                .getHeader("Location");
    }
}

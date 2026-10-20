package io.github.whocares0604.spacedrepetition.deck;

import io.github.whocares0604.spacedrepetition.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasItem;
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
class DeckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsAndReadsDeck() throws Exception {
        String location = createDeck("Spanish");

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Spanish"))
                .andExpect(jsonPath("$.createdAt").value(notNullValue()));

        mockMvc.perform(get("/api/decks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name").value(hasItem("Spanish")));
    }

    @Test
    void updatesDeck() throws Exception {
        String location = createDeck("Old name");

        mockMvc.perform(put(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "New name"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New name"));
    }

    @Test
    void deletesDeck() throws Exception {
        String location = createDeck("To delete");

        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(location))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsNotFoundForUnknownDeck() throws Exception {
        mockMvc.perform(get("/api/decks/999999"))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/decks/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsBlankName() throws Exception {
        mockMvc.perform(post("/api/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": " "}
                                """))
                .andExpect(status().isBadRequest());
    }

    private String createDeck(String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "%s"}
                                """.formatted(name)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name").value(name))
                .andReturn();
        return result.getResponse().getHeader("Location");
    }
}

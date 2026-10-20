package io.github.whocares0604.spacedrepetition.deck;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeckRequest(
        @NotBlank
        @Size(max = 255)
        String name
) {
}

package io.github.whocares0604.spacedrepetition.card;

import jakarta.validation.constraints.NotBlank;

public record CardRequest(
        @NotBlank
        String front,

        @NotBlank
        String back
) {
}

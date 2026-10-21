package io.github.whocares0604.spacedrepetition.review;

import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        @NotNull
        ReviewGrade grade
) {
}

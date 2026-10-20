package io.github.whocares0604.spacedrepetition.review;

/**
 * Scheduling state of a card.
 *
 * @param repetitions  number of successful reviews in a row
 * @param intervalDays days until the next review
 * @param easeFactor   how fast the interval grows, never below {@link Sm2Scheduler#MIN_EASE_FACTOR}
 */
public record ReviewState(int repetitions, int intervalDays, double easeFactor) {

    public static ReviewState initial() {
        return new ReviewState(0, 0, Sm2Scheduler.INITIAL_EASE_FACTOR);
    }
}

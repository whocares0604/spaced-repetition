package io.github.whocares0604.spacedrepetition.review;

/**
 * Simplified SM-2 algorithm with four grades.
 *
 * <ul>
 *     <li>AGAIN: the card was forgotten, start again from a 1-day interval.</li>
 *     <li>HARD: remembered with difficulty, the interval grows slowly.</li>
 *     <li>GOOD: remembered, intervals are 1 day, 6 days, then previous interval * ease factor.</li>
 *     <li>EASY: remembered easily, the interval grows faster than with GOOD.</li>
 * </ul>
 */
public class Sm2Scheduler {

    public static final double INITIAL_EASE_FACTOR = 2.5;
    public static final double MIN_EASE_FACTOR = 1.3;

    private static final double HARD_INTERVAL_MULTIPLIER = 1.2;
    private static final double EASY_INTERVAL_MULTIPLIER = 1.3;

    public ReviewState next(ReviewState current, ReviewGrade grade) {
        return switch (grade) {
            case AGAIN -> new ReviewState(
                    0,
                    1,
                    changeEaseFactor(current.easeFactor(), -0.20));
            case HARD -> new ReviewState(
                    current.repetitions() + 1,
                    hardInterval(current),
                    changeEaseFactor(current.easeFactor(), -0.15));
            case GOOD -> new ReviewState(
                    current.repetitions() + 1,
                    goodInterval(current),
                    current.easeFactor());
            case EASY -> new ReviewState(
                    current.repetitions() + 1,
                    easyInterval(current),
                    changeEaseFactor(current.easeFactor(), 0.15));
        };
    }

    private int goodInterval(ReviewState current) {
        if (current.repetitions() == 0) {
            return 1;
        }
        if (current.repetitions() == 1) {
            return 6;
        }
        return (int) Math.round(current.intervalDays() * current.easeFactor());
    }

    private int hardInterval(ReviewState current) {
        if (current.repetitions() == 0) {
            return 1;
        }
        int interval = (int) Math.round(current.intervalDays() * HARD_INTERVAL_MULTIPLIER);
        return Math.max(interval, current.intervalDays() + 1);
    }

    private int easyInterval(ReviewState current) {
        int goodInterval = goodInterval(current);
        int interval = (int) Math.round(goodInterval * EASY_INTERVAL_MULTIPLIER);
        return Math.max(interval, goodInterval + 1);
    }

    private double changeEaseFactor(double easeFactor, double delta) {
        double newEaseFactor = Math.max(MIN_EASE_FACTOR, easeFactor + delta);
        // Round to 2 decimals to avoid values like 2.3499999999999996.
        return Math.round(newEaseFactor * 100) / 100.0;
    }
}

package io.github.whocares0604.spacedrepetition.review;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Sm2SchedulerTest {

    private final Sm2Scheduler scheduler = new Sm2Scheduler();

    @Test
    void initialStateHasNoRepetitionsAndDefaultEaseFactor() {
        assertThat(ReviewState.initial()).isEqualTo(new ReviewState(0, 0, 2.5));
    }

    @Test
    void goodAnswersGiveOneDayThenSixDaysThenGrowByEaseFactor() {
        ReviewState first = scheduler.next(ReviewState.initial(), ReviewGrade.GOOD);
        ReviewState second = scheduler.next(first, ReviewGrade.GOOD);
        ReviewState third = scheduler.next(second, ReviewGrade.GOOD);

        assertThat(first).isEqualTo(new ReviewState(1, 1, 2.5));
        assertThat(second).isEqualTo(new ReviewState(2, 6, 2.5));
        assertThat(third).isEqualTo(new ReviewState(3, 15, 2.5));
    }

    @Test
    void againResetsRepetitionsAndLowersEaseFactor() {
        ReviewState learned = new ReviewState(3, 15, 2.5);

        ReviewState next = scheduler.next(learned, ReviewGrade.AGAIN);

        assertThat(next).isEqualTo(new ReviewState(0, 1, 2.3));
    }

    @Test
    void hardGrowsIntervalSlowlyAndLowersEaseFactor() {
        assertThat(scheduler.next(ReviewState.initial(), ReviewGrade.HARD))
                .isEqualTo(new ReviewState(1, 1, 2.35));

        assertThat(scheduler.next(new ReviewState(2, 6, 2.5), ReviewGrade.HARD))
                .isEqualTo(new ReviewState(3, 7, 2.35));
    }

    @Test
    void easyGrowsIntervalFasterAndRaisesEaseFactor() {
        assertThat(scheduler.next(ReviewState.initial(), ReviewGrade.EASY))
                .isEqualTo(new ReviewState(1, 2, 2.65));

        assertThat(scheduler.next(new ReviewState(2, 6, 2.5), ReviewGrade.EASY))
                .isEqualTo(new ReviewState(3, 20, 2.65));
    }

    @Test
    void betterGradeNeverGivesShorterInterval() {
        ReviewState current = new ReviewState(2, 6, 2.5);

        int again = scheduler.next(current, ReviewGrade.AGAIN).intervalDays();
        int hard = scheduler.next(current, ReviewGrade.HARD).intervalDays();
        int good = scheduler.next(current, ReviewGrade.GOOD).intervalDays();
        int easy = scheduler.next(current, ReviewGrade.EASY).intervalDays();

        assertThat(again).isLessThan(hard);
        assertThat(hard).isLessThan(good);
        assertThat(good).isLessThan(easy);
    }

    @Test
    void easeFactorNeverGoesBelowMinimum() {
        ReviewState almostMinimum = new ReviewState(3, 10, 1.35);

        ReviewState afterAgain = scheduler.next(almostMinimum, ReviewGrade.AGAIN);
        ReviewState afterHard = scheduler.next(afterAgain, ReviewGrade.HARD);

        assertThat(afterAgain.easeFactor()).isEqualTo(1.3);
        assertThat(afterHard.easeFactor()).isEqualTo(1.3);
    }
}

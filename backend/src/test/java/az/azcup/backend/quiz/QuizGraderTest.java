package az.azcup.backend.quiz;

import az.azcup.backend.entity.QuizQuestion;
import az.azcup.backend.entity.QuizQuestionType;
import az.azcup.backend.service.QuizGrader;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// Cavabın yoxlanması (QuizGrader) — verilənlər bazasız, sürətli unit testlər.
class QuizGraderTest {

    private static QuizQuestion choice(int correctIndex) {
        QuizQuestion q = new QuizQuestion();
        q.setType(QuizQuestionType.CHOICE);
        q.getOptions().addAll(java.util.List.of("A", "B", "C", "D", "E"));
        q.setCorrectIndex(correctIndex);
        return q;
    }

    private static QuizQuestion numeric(String correctText) {
        QuizQuestion q = new QuizQuestion();
        q.setType(QuizQuestionType.NUMERIC);
        q.setCorrectText(correctText);
        return q;
    }

    @Test
    void choiceMatchesOnlyTheCorrectIndex() {
        QuizQuestion q = choice(2);
        assertThat(QuizGrader.isCorrect(q, "2")).isTrue();
        assertThat(QuizGrader.isCorrect(q, " 2 ")).isTrue();
        assertThat(QuizGrader.isCorrect(q, "1")).isFalse();
        assertThat(QuizGrader.isCorrect(q, "C")).isFalse();
        assertThat(QuizGrader.isCorrect(q, "2.0")).isFalse();
    }

    @Test
    void blankOrMissingAnswerIsWrong() {
        assertThat(QuizGrader.isCorrect(choice(0), null)).isFalse();
        assertThat(QuizGrader.isCorrect(choice(0), "  ")).isFalse();
        assertThat(QuizGrader.isCorrect(numeric("0"), "")).isFalse();
        assertThat(QuizGrader.isCorrect(numeric("0"), null)).isFalse();
    }

    @Test
    void numericIgnoresSpacingCommaAndTrailingZeros() {
        QuizQuestion q = numeric("12");
        assertThat(QuizGrader.isCorrect(q, "12")).isTrue();
        assertThat(QuizGrader.isCorrect(q, " 12 ")).isTrue();
        assertThat(QuizGrader.isCorrect(q, "12.0")).isTrue();
        assertThat(QuizGrader.isCorrect(q, "12,00")).isTrue();
        assertThat(QuizGrader.isCorrect(q, "+12")).isTrue();
        assertThat(QuizGrader.isCorrect(q, "13")).isFalse();
        assertThat(QuizGrader.isCorrect(q, "1 2")).isFalse();
    }

    @Test
    void numericSupportsSeveralAcceptedAnswers() {
        QuizQuestion q = numeric("3.5|7/2");
        assertThat(QuizGrader.isCorrect(q, "3,5")).isTrue();
        assertThat(QuizGrader.isCorrect(q, "7/2")).isTrue();
        assertThat(QuizGrader.isCorrect(q, "3.6")).isFalse();
    }

    @Test
    void numericTextAnswersAreCaseInsensitive() {
        QuizQuestion q = numeric("Bəli");
        assertThat(QuizGrader.isCorrect(q, "bəli")).isTrue();
        assertThat(QuizGrader.isCorrect(q, "  BƏLİ  ")).isTrue();
        assertThat(QuizGrader.isCorrect(q, "xeyr")).isFalse();
        // i / İ / I / ı fərqi (Azərbaycan klaviaturası) nəzərə alınmır
        assertThat(QuizGrader.isCorrect(numeric("print"), "PRINT")).isTrue();
        assertThat(QuizGrader.isCorrect(numeric("PRINT"), "prınt")).isTrue();
    }

    @Test
    void hugeExponentIsNotExpandedAndSimplyDoesNotMatch() {
        // "1e999999999" BigDecimal kimi açılsaydı toPlainString() yaddaşı doldururdu; indi sətir kimi müqayisə olunur.
        QuizQuestion q = numeric("1");
        assertThat(QuizGrader.isCorrect(q, "1e999999999")).isFalse();
        assertThat(QuizGrader.isCorrect(numeric("1e999999999"), "1e999999999")).isTrue();
    }
}

package az.azcup.backend.service;

import az.azcup.backend.entity.QuizQuestion;
import az.azcup.backend.entity.QuizQuestionType;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.Pattern;

// Bir sualın cavabını yoxlayan təmiz (verilənlər bazasından asılı olmayan) məntiq — ayrıca unit testlə yoxlanılır.
public final class QuizGrader {

    // Yalnız adi onluq yazılışı ədəd sayırıq. "1e999999999" kimi elmi yazılışı BigDecimal-a verməyin təhlükəsi var:
    // toPlainString() yüz milyonlarla simvol yarada bilər. Ona görə uzunluq və forma məhdudlaşdırılır.
    private static final Pattern PLAIN_NUMBER = Pattern.compile("[+-]?\\d{1,30}(\\.\\d{1,30})?");

    private QuizGrader() {
    }

    // Şagirdin cavabı düzgündürmü. null/boş cavab həmişə yanlışdır.
    public static boolean isCorrect(QuizQuestion question, String answer) {
        if (answer == null || answer.isBlank()) {
            return false;
        }
        if (question.getType() == QuizQuestionType.CHOICE) {
            Integer chosen = parseIndex(answer);
            return chosen != null && chosen.equals(question.getCorrectIndex());
        }
        String given = normalize(answer);
        String accepted = question.getCorrectText();
        if (accepted == null || accepted.isBlank()) {
            return false;
        }
        for (String candidate : accepted.split("\\|")) {
            if (!candidate.isBlank() && given.equals(normalize(candidate))) {
                return true;
            }
        }
        return false;
    }

    // Müqayisə üçün cavabı eyniləşdirir: boşluqlar, vergül/nöqtə fərqi və sonundakı sıfırlar nəzərə alınmır
    // ("12", "12.0", " 12,00 " eynidir); ədəd olmayan cavab kiçik hərflə müqayisə olunur.
    static String normalize(String raw) {
        String text = raw.trim().replace(',', '.').replaceAll("\\s+", " ");
        if (PLAIN_NUMBER.matcher(text).matches()) {
            return new BigDecimal(text).stripTrailingZeros().toPlainString();
        }
        // Azərbaycan hərfləri: Locale.ROOT ilə "İ" kiçildəndə "i" + birləşdirici nöqtə alınır və "bəli" ilə uyğun gəlmir; "I"/"ı" da
        // klaviaturada qarışdırılır. Ona görə İ, I, ı və i eyniləşdirilir (cavab açarında belə fərq nəzərə alınmır).
        return text.replace('İ', 'i').replace('I', 'i').replace('ı', 'i').toLowerCase(Locale.ROOT);
    }

    private static Integer parseIndex(String answer) {
        try {
            return Integer.valueOf(answer.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

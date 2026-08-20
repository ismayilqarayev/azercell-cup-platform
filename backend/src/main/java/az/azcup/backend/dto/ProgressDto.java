package az.azcup.backend.dto;

import java.util.Objects;

// "GET /api/me/progress" cavabında hər mövzu üçün bir sətir — şagirdin
// hər mövzuda neçə problemi (total-dan neçəsini) həll etdiyini göstərir.
// Frontend-də irəliləyiş zolağı/faizi (məs. "7 / 10 həll edilib") üçün istifadə olunur.
public class ProgressDto {

    // Mövzunun slug-u.
    private final String topicSlug;
    // Mövzunun başlığı.
    private final String topicTitle;
    // Mövzudakı ümumi problem sayı.
    private final long total;
    // İstifadəçinin bu mövzuda həll etdiyi problem sayı.
    private final long solved;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public ProgressDto(String topicSlug, String topicTitle, long total, long solved) {
        this.topicSlug = topicSlug;
        this.topicTitle = topicTitle;
        this.total = total;
        this.solved = solved;
    }

    // topicSlug sahəsinin dəyərini qaytarır.
    public String getTopicSlug() {
        return topicSlug;
    }

    // topicTitle sahəsinin dəyərini qaytarır.
    public String getTopicTitle() {
        return topicTitle;
    }

    // total sahəsinin dəyərini qaytarır.
    public long getTotal() {
        return total;
    }

    // solved sahəsinin dəyərini qaytarır.
    public long getSolved() {
        return solved;
    }

    // İki ProgressDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProgressDto that = (ProgressDto) o;
        return total == that.total
            && solved == that.solved
            && Objects.equals(topicSlug, that.topicSlug)
            && Objects.equals(topicTitle, that.topicTitle);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(topicSlug, topicTitle, total, solved);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "ProgressDto{" +
            "topicSlug='" + topicSlug + '\'' +
            ", topicTitle='" + topicTitle + '\'' +
            ", total=" + total +
            ", solved=" + solved +
            '}';
    }
}

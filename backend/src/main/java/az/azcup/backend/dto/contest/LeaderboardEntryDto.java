package az.azcup.backend.dto.contest;

import java.time.Instant;
import java.util.Objects;

// Reytinq cədvəlində TƏK BİR sətri (bir iştirakçını) təmsil edir.
// ContestService.leaderboard bu tipdən ibarət siyahını CƏMİ BALA görə
// azalan, bərabərlikdə isə həmin bala İLK ÇATDIĞI vaxta görə artan sırada
// (kim daha tez çatıbsa öndə) qaytarır.
public class LeaderboardEntryDto {

    // Sıralamadakı yeri (1-dən başlayaraq) — ContestService tərəfindən
    // siyahı artıq sıralandıqdan sonra doldurulur.
    private final int rank;
    // İştirakçının istifadəçi ID-si.
    private final Long userId;
    // İştirakçının tam adı.
    private final String fullName;
    // Toplam qazanılan bal.
    private final int totalPoints;
    // Neçə fərqli məsələni tam həll etdiyi.
    private final int solvedCount;
    // Cəmi balına ÇATDIĞI (sonuncu balı qazandığı) vaxt — bərabərlikləri
    // izah etmək üçün frontend-də göstərilə bilər.
    private final Instant scoreReachedAt;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public LeaderboardEntryDto(int rank, Long userId, String fullName, int totalPoints, int solvedCount, Instant scoreReachedAt) {
        this.rank = rank;
        this.userId = userId;
        this.fullName = fullName;
        this.totalPoints = totalPoints;
        this.solvedCount = solvedCount;
        this.scoreReachedAt = scoreReachedAt;
    }

    // rank sahəsinin dəyərini qaytarır.
    public int getRank() {
        return rank;
    }

    // userId sahəsinin dəyərini qaytarır.
    public Long getUserId() {
        return userId;
    }

    // fullName sahəsinin dəyərini qaytarır.
    public String getFullName() {
        return fullName;
    }

    // totalPoints sahəsinin dəyərini qaytarır.
    public int getTotalPoints() {
        return totalPoints;
    }

    // solvedCount sahəsinin dəyərini qaytarır.
    public int getSolvedCount() {
        return solvedCount;
    }

    // scoreReachedAt sahəsinin dəyərini qaytarır.
    public Instant getScoreReachedAt() {
        return scoreReachedAt;
    }

    // İki LeaderboardEntryDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LeaderboardEntryDto that = (LeaderboardEntryDto) o;
        return rank == that.rank
            && totalPoints == that.totalPoints
            && solvedCount == that.solvedCount
            && Objects.equals(userId, that.userId)
            && Objects.equals(fullName, that.fullName)
            && Objects.equals(scoreReachedAt, that.scoreReachedAt);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(rank, userId, fullName, totalPoints, solvedCount, scoreReachedAt);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "LeaderboardEntryDto{" +
            "rank=" + rank +
            ", userId=" + userId +
            ", fullName='" + fullName + '\'' +
            ", totalPoints=" + totalPoints +
            ", solvedCount=" + solvedCount +
            ", scoreReachedAt=" + scoreReachedAt +
            '}';
    }
}

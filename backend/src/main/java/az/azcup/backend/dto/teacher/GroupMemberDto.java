package az.azcup.backend.dto.teacher;

import java.time.Instant;
import java.util.Objects;

// Bir qrupun üzv (şagird) siyahısındakı tək sətir — irəliləyiş cədvəlində
// ("Qruplarım" > qrup seçildikdə) hər şagirdin ümumi həll etdiyi problem
// sayı ilə birlikdə göstərilir.
public class GroupMemberDto {

    // Üzvlük sətrinin ID-si (silmə əməliyyatı bunu ID kimi istifadə edir).
    private final Long membershipId;
    // Şagirdin verilənlər bazasındakı ID-si.
    private final Long studentId;
    // Şagirdin tam adı.
    private final String fullName;
    // Şagirdin e-poçtu.
    private final String email;
    // Şagirdin uğurla (ACCEPTED statusu ilə) həll etdiyi problemlərin sayı.
    private final long totalSolved;
    // Şagirdin qrupa əlavə olunma vaxtı.
    private final Instant joinedAt;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public GroupMemberDto(
        Long membershipId,
        Long studentId,
        String fullName,
        String email,
        long totalSolved,
        Instant joinedAt
    ) {
        this.membershipId = membershipId;
        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
        this.totalSolved = totalSolved;
        this.joinedAt = joinedAt;
    }

    // membershipId sahəsinin dəyərini qaytarır.
    public Long getMembershipId() {
        return membershipId;
    }

    // studentId sahəsinin dəyərini qaytarır.
    public Long getStudentId() {
        return studentId;
    }

    // fullName sahəsinin dəyərini qaytarır.
    public String getFullName() {
        return fullName;
    }

    // email sahəsinin dəyərini qaytarır.
    public String getEmail() {
        return email;
    }

    // totalSolved sahəsinin dəyərini qaytarır.
    public long getTotalSolved() {
        return totalSolved;
    }

    // joinedAt sahəsinin dəyərini qaytarır.
    public Instant getJoinedAt() {
        return joinedAt;
    }

    // İki GroupMemberDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupMemberDto that = (GroupMemberDto) o;
        return totalSolved == that.totalSolved
            && Objects.equals(membershipId, that.membershipId)
            && Objects.equals(studentId, that.studentId)
            && Objects.equals(fullName, that.fullName)
            && Objects.equals(email, that.email)
            && Objects.equals(joinedAt, that.joinedAt);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(membershipId, studentId, fullName, email, totalSolved, joinedAt);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "GroupMemberDto{" +
            "membershipId=" + membershipId +
            ", studentId=" + studentId +
            ", fullName='" + fullName + '\'' +
            ", email='" + email + '\'' +
            ", totalSolved=" + totalSolved +
            ", joinedAt=" + joinedAt +
            '}';
    }
}

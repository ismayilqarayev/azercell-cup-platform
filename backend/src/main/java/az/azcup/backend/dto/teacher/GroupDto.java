package az.azcup.backend.dto.teacher;

import java.time.Instant;
import java.util.Objects;

// "Qruplarım" ekranındakı qrup siyahısı üçün — hər qrupun adı, sahib
// müəllimi (admin görünüşündə lazımdır) və üzv sayı birlikdə göstərilir.
public class GroupDto {

    // Qrupun verilənlər bazasındakı ID-si.
    private final Long id;
    // Qrupun adı.
    private final String name;
    // Qrupun sahib müəlliminin ID-si.
    private final Long teacherId;
    // Qrupun sahib müəlliminin tam adı (admin panelindəki overview üçün).
    private final String teacherName;
    // Qrupdakı şagird sayı.
    private final long memberCount;
    // Qrupun yaradıldığı vaxt.
    private final Instant createdAt;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public GroupDto(Long id, String name, Long teacherId, String teacherName, long memberCount, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.memberCount = memberCount;
        this.createdAt = createdAt;
    }

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
    }

    // name sahəsinin dəyərini qaytarır.
    public String getName() {
        return name;
    }

    // teacherId sahəsinin dəyərini qaytarır.
    public Long getTeacherId() {
        return teacherId;
    }

    // teacherName sahəsinin dəyərini qaytarır.
    public String getTeacherName() {
        return teacherName;
    }

    // memberCount sahəsinin dəyərini qaytarır.
    public long getMemberCount() {
        return memberCount;
    }

    // createdAt sahəsinin dəyərini qaytarır.
    public Instant getCreatedAt() {
        return createdAt;
    }

    // İki GroupDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupDto that = (GroupDto) o;
        return memberCount == that.memberCount
            && Objects.equals(id, that.id)
            && Objects.equals(name, that.name)
            && Objects.equals(teacherId, that.teacherId)
            && Objects.equals(teacherName, that.teacherName)
            && Objects.equals(createdAt, that.createdAt);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(id, name, teacherId, teacherName, memberCount, createdAt);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "GroupDto{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", teacherId=" + teacherId +
            ", teacherName='" + teacherName + '\'' +
            ", memberCount=" + memberCount +
            ", createdAt=" + createdAt +
            '}';
    }
}

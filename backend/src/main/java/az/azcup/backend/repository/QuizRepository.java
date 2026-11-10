package az.azcup.backend.repository;

import az.azcup.backend.entity.Quiz;
import az.azcup.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    // Şagirdlərə göstərilən siyahı: yalnız dərc olunmuşlar, yenisi əvvəldə.
    List<Quiz> findByPublishedTrueOrderByCreatedAtDesc();

    // Müəllimin öz testləri (idarəetmə siyahısı).
    List<Quiz> findByCreatedByOrderByUpdatedAtDesc(User createdBy);

    // Admin üçün bütün testlər.
    List<Quiz> findAllByOrderByUpdatedAtDesc();

    // Əvvəlki buraxılışda avtomatik yaradılmış hazır testlərin ID-ləri (təmizləmə üçün, bax: QuizSeedCleanup).
    @Query("select q.id from Quiz q where q.seedKey is not null")
    List<Long> findSeededIds();
}
